package com.ngn.utils.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.api.media.ApiMediaModel;
import com.ngn.api.media.ApiMediaService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.GeneralUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

public class ActtachmentForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private Span spanTitle = new Span("Danh sách file");
	private Grid<ApiMediaModel> grid = new Grid<ApiMediaModel>(ApiMediaModel.class,false);
	private List<ApiMediaModel> listModel = new ArrayList<ApiMediaModel>();
	
	private List<String> listDelete = new ArrayList<String>();
	
	private List<String> listActtachment;
	private boolean checkView = false;
	public ActtachmentForm(List<String> listActtachment,boolean checkView) {
		this.checkView = checkView;
		buildLayout();
		configComponent();
		if(!listActtachment.isEmpty()) {
			this.listActtachment = listActtachment;
			loadData();
		}
	}
	
	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.setHeight("800px");
		
		
		spanTitle.getStyle().setFontWeight(600);
		
		this.add(spanTitle,createLayoutGrid());
		
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		listModel.clear();
		listActtachment.forEach(model->{
			ApiMediaModel apiMediaModel = requestMedia(model);
			if(apiMediaModel != null) {
				listModel.add(apiMediaModel);
			}
		});
		
		grid.setItems(listModel);
	}
	
	private ApiMediaModel requestMedia(String id) {
		try {
			ApiResultResponse<ApiMediaModel> data = ApiMediaService.getInfoFile(id);
			if(data.isSuccess()) {
				return data.getResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	private Component createLayoutGrid() {
		grid = new Grid<ApiMediaModel>(ApiMediaModel.class,false);
		
		grid.addComponentColumn(model ->{
			VerticalLayout vLayout = new VerticalLayout();
			HorizontalLayout hLayout = new HorizontalLayout();
			
			Span spanName = new Span(model.getFileName());
			Icon icon = FontAwesome.Solid.PAPERCLIP.create();
			icon.setSize("13px");
			hLayout.add(icon,spanName);
			
			TextField txtDesr = new TextField("Mô tả");
			if(!(model.getFileDescription() == null)) {
				txtDesr.setValue(model.getFileDescription());
			}
			txtDesr.setWidthFull();
			txtDesr.addThemeVariants(TextFieldVariant.LUMO_SMALL);
			
			hLayout.setWidthFull();
			hLayout.setPadding(false);
			
			vLayout.setWidthFull();
			vLayout.setPadding(false);
			vLayout.setSpacing(false);
			
			vLayout.add(hLayout,txtDesr);
			
			return vLayout;
		}).setHeader("Tên file");
		
		String widthColumn = "150px";
		if(checkView) {
			widthColumn = "100px";
		}
		
		grid.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();
			ButtonTemplate btnDelete = new ButtonTemplate(FontAwesome.Solid.REMOVE.create());
			btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_SMALL);
			btnDelete.setTooltipText("Xóa");
			btnDelete.getStyle().setMarginLeft("-10px");
			btnDelete.addClickListener(e->{
				listDelete.add(model.getId());
				listActtachment.remove(model.getId());
				loadData();
				fireEvent(new ClickEvent(this, false));
			});
			
			
			ButtonTemplate btnViewPDF = new ButtonTemplate(FontAwesome.Solid.EYE.create());
			btnViewPDF.addThemeVariants(ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_SMALL);
			btnViewPDF.setTooltipText("Xem file");
			btnViewPDF.setEnabled(false);
			
			boolean isPDF = model.getFilePath().endsWith(".pdf");
			
			
			if(model.getFileType().equals("application/pdf") || isPDF) {
				btnViewPDF.setEnabled(true);
			}
			
			btnViewPDF.addClickListener(e->{
				loadPDF(model.getFileName(), model.getId());
			});
			
			
			ButtonTemplate btnDownloadFile = new ButtonTemplate(FontAwesome.Solid.DOWNLOAD.create());
			btnDownloadFile.addThemeVariants(ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_SMALL);
			btnDownloadFile.setTooltipText("Tải file");
			btnDownloadFile.setDownload();
			btnDownloadFile.addClickListener(e->{
				try {
					
					btnDownloadFile.downLoad(GeneralUtil.getStreamResource(model.getFileName(), GeneralUtil.base64ToByteArray(getBase64Data(model.getFileName(), model.getId()))));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
			if(checkView) {
				hLayout.add(btnViewPDF,btnDownloadFile,btnDownloadFile.getAnchor());
			}else {
				hLayout.add(btnViewPDF,btnDownloadFile,btnDownloadFile.getAnchor(),btnDelete);
			}
			
			hLayout.getStyle().setMarginTop("40px");
			return hLayout;
		}).setHeader("Thao tác").setWidth(widthColumn).setFlexGrow(0);
		
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		
		return grid;
	}
	
	public List<String> getListDelete(){
		return this.listDelete;
	}
	
	
	private void loadPDF(String fileName,String idFile) {
		try {
			ApiResultResponse<String> data = ApiMediaService.getContentFile(idFile);
			createLayoutViewPDF(fileName, data.getResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getBase64Data(String fileName,String idFile) {
		try {
			ApiResultResponse<String> data = ApiMediaService.getContentFile(idFile);
			if(data.isSuccess()) {
				return data.getResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void createLayoutViewPDF(String fileName,String base64) {
		DialogTemplate dialogTemplate = new DialogTemplate("Xem file");
		dialogTemplate.setSizeFull();
		
		ViewAttachmentForm viewAttachmentForm = new ViewAttachmentForm(fileName, base64);
		
		dialogTemplate.add(viewAttachmentForm);
		dialogTemplate.open();
		dialogTemplate.getBtnSave().setVisible(false);
		
	}
	public Span getSpanTitle() {
		return spanTitle;
	}

	public void setSpanTitle(String spanTitle) {
		this.spanTitle.setText(spanTitle);
	}

}

























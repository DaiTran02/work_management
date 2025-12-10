package com.ngn.tdnv.doc.forms.create_task;

import java.util.ArrayList;
import java.util.List;

import com.ngn.api.media.ApiMediaModel;
import com.ngn.api.media.ApiMediaService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.components.ViewAttachmentForm;
import com.ngn.utils.components.ViewFileDocxForm;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;

public class ViewDocFromFile extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private ComboBox<ApiMediaModel> cmbFile = new ComboBox<ApiMediaModel>("Danh sách file");
	private List<ApiMediaModel> listFileCmb = new ArrayList<ApiMediaModel>();
	
	private ViewAttachmentForm viewAttachmentForm;
	private ViewFileDocxForm viewFileDocxForm;
	
	private List<String> listFile;
	public ViewDocFromFile(List<String> listFile) {
		this.setSizeFull();
		this.setPadding(false);
		this.listFile = listFile;
		loadData();
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		if(!listFileCmb.isEmpty()) {
			viewAttachmentForm = new ViewAttachmentForm(cmbFile.getValue().getFileName(), loadPDF(cmbFile.getValue().getId()));
		}
		
		cmbFile.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		cmbFile.setWidthFull();
		
		this.setSpacing(false);
		this.setPadding(false);
		this.add(cmbFile);
		
		
		if(!listFileCmb.isEmpty()) {
			if(cmbFile.getValue().getFileType().equals("application/pdf") || cmbFile.getValue().isPdfFile()) {
				this.add(viewAttachmentForm);
			}else {
				viewFileDocxForm = new ViewFileDocxForm(cmbFile.getValue().getFileName(), loadPDF(cmbFile.getValue().getId()));
				this.add(viewFileDocxForm);
			}
		}
	}

	@Override
	public void configComponent() {
		cmbFile.addValueChangeListener(e->{
			this.removeAll();
			viewAttachmentForm = new ViewAttachmentForm(cmbFile.getValue().getFileName(), loadPDF(cmbFile.getValue().getId()));
			this.add(cmbFile);
			if(cmbFile.getValue().getFileType().equals("application/pdf") || cmbFile.getValue().isPdfFile()) {
				this.add(viewAttachmentForm);
			}else {
				viewFileDocxForm = new ViewFileDocxForm(cmbFile.getValue().getFileName(), loadPDF(cmbFile.getValue().getId()));
				this.add(viewFileDocxForm);
			}
		});
		
	}
	
	public void loadData() {
		listFileCmb.clear();
		listFile.stream().forEach(model->{
			ApiMediaModel data = getMedia(model);
			if(data != null) {
				listFileCmb.add(data);
			}
		});
		
		cmbFile.setItems(listFileCmb);
		cmbFile.setItemLabelGenerator(ApiMediaModel::getFileName);
		if(!listFileCmb.isEmpty()) {
			cmbFile.setValue(listFileCmb.get(0));
		}
	}

	public List<ApiMediaModel> getListFileCmb() {
		return listFileCmb;
	}

	private ApiMediaModel getMedia(String idMedia) {
		try {
			ApiResultResponse<ApiMediaModel> data = ApiMediaService.getInfoFile(idMedia);
			return data.getResult();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String loadPDF(String id) {
		ApiResultResponse<String> data = ApiMediaService.getContentFile(id);
		if(data.isSuccess()) {
			return data.getResult();
		}else {
			return "";
		}
	}

	public ComboBox<ApiMediaModel> getCmbFile() {
		return cmbFile;
	}

}

package com.ngn.tdnv.task.forms.details;

import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.tdnv.task.models.TaskProcessModel;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexDirection;
import com.vaadin.flow.dom.Style.FlexWrap;

public class TaskProgressViewForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean isUiMobile = false;
	private ButtonTemplate btnUpdateProgress = new ButtonTemplate("Cập nhật tiến độ",FontAwesome.Solid.EDIT.create());
	private ButtonTemplate btnAttachment = new ButtonTemplate("Tệp đính kèm",FontAwesome.Solid.PAPERCLIP.create());
	private ButtonTemplate btnHistoryProgress = new ButtonTemplate("Lịch sử tiến độ",FontAwesome.Solid.HISTORY.create());
	private VerticalLayout vLayout = new VerticalLayout();
	
	private TaskOutputModel outputTaskModel;
	private Boolean checkChange = false;
	
	private String idTask;
	private TaskProcessModel taskProcessModel;
	private boolean checkTaskSupport;
	private String state;
	private boolean isViewHistory = false;
	public TaskProgressViewForm(String idTask,TaskProcessModel taskProcessModel,boolean checkTaskSupport,String state,boolean isViewHistory) {
		this.taskProcessModel = taskProcessModel;
		this.idTask = idTask;
		this.checkTaskSupport = checkTaskSupport;
		this.state = state;
		this.isViewHistory = isViewHistory;
		checkUIMobile();
		buildLayout();
		configComponent();
		checkPermission();
	}
	
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		vLayout.setWidthFull();
		this.add(vLayout);
		createLayout();
	}

	@Override
	public void configComponent() {
		btnUpdateProgress.addClickListener(e->{
			openDialogUpdateProgressTask();
		});
		
		btnHistoryProgress.addClickListener(e->{
			openDialogViewHistory();
		});
	}
	
	public void loadData() {
		ApiResultResponse<ApiOutputTaskModel> data = ApiTaskService.getAtask(idTask);
		if(data.isSuccess()) {
			outputTaskModel = new TaskOutputModel(data.getResult());
			fireEvent(new ClickEvent(this,false));
		}
		if(outputTaskModel.getStatus().equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
			btnUpdateProgress.setVisible(false);
		}
		taskProcessModel = outputTaskModel.getProcesses().isEmpty() ? null : outputTaskModel.getProcesses().get(0);
		if(taskProcessModel != null) {
			createLayout();
		}
	}
	
	private void checkPermission() {
		if(checkTaskSupport) {
			btnUpdateProgress.setVisible(false);
		}
		if(state.equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
			btnUpdateProgress.setVisible(false);
		}
		
		if(state.equals(StatusTaskEnum.TAMHOAN.getKey())) {
			btnUpdateProgress.setEnabled(false);
		}
		
		if(state.equals(StatusTaskEnum.TUCHOITHUCHIEN.getKey())) {
			btnUpdateProgress.setEnabled(false);
		}
		
		if(isViewHistory == false) {
			btnHistoryProgress.setVisible(false);
		}
	}
	
	private void checkUIMobile() {
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					isUiMobile = true;
				}
			});
		} catch (Exception e) {
		}
	}
	
	private void createLayout() {
		ProgressBar progressBar = new ProgressBar();
		progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
		progressBar.setValue((float)taskProcessModel.getPercent()/100);
		
		NativeLabel pNativeLabel = new NativeLabel("Tiến độ hiện tại");
		pNativeLabel.setId("pblabel");
		pNativeLabel.getStyle().setColor("#0079d6");
		
		progressBar.getElement().setAttribute("aria-labelledby", "pblabel");
		Span progressBarLabelValue = new Span(taskProcessModel.getPercent()+"%");
		progressBarLabelValue.getStyle().setColor("#0079d6");
		HorizontalLayout progressBarLabel = new HorizontalLayout(pNativeLabel, progressBarLabelValue);
		progressBarLabel.setJustifyContentMode(JustifyContentMode.BETWEEN);
		
		btnAttachment = new ButtonTemplate("Tệp đính kèm ("+taskProcessModel.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		
		btnAttachment.getStyle().setMarginLeft("auto");
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(taskProcessModel.getAttachments());
		});
		
		
		Div div = new Div();
		div.add(new H4("*Thông tin tiến độ hiện tại của nhiệm vụ"),progressBarLabel,progressBar,createLayoutKeyValue("Diễn giải: ", taskProcessModel.getExplainText(), null));
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setWidthFull();
		horizontalLayout.add(createLayoutKeyValue("Đơn vị nhập: ", taskProcessModel.getCreator() == null ? "Chưa cập nhật" 
				: taskProcessModel.getCreator().getOrganizationName() + " (" + taskProcessModel.getCreator().getOrganizationUserName() +")", null),
				createLayoutKeyValue("Ngày nhập: ", taskProcessModel.getCreateTimeText(), null),btnAttachment,btnHistoryProgress,btnUpdateProgress);
		
		if(isUiMobile) {
			horizontalLayout.getStyle().setDisplay(Display.FLEX).setFlexWrap(FlexWrap.WRAP).set("gap", "50px");
		}
		
		div.add(horizontalLayout);
		div.setWidthFull();
		
		vLayout.removeAll();
		vLayout.add(div);
	}
	
	private Component createLayoutKeyValue(String header,String content,String style) {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);
		spanHeader.setMinWidth("70px");
		
		Span spanContent = new Span(content);
		
		if(style != null) {
			spanContent.getElement().getThemeList().add(style);
		}
		
		if(isUiMobile) {
			hLayout.getStyle().setDisplay(Display.FLEX).setFlexDirection(FlexDirection.COLUMN);
		}
		
		hLayout.add(spanHeader,spanContent);
		
		
		return hLayout;
	}
	
	private void openDialogUpdateProgressTask() {
		DialogTemplate dialogTemplate = new DialogTemplate("CẬP NHẬT TIẾN ĐỘ");
		TaskProgressForm taskProgressForm = new TaskProgressForm(idTask);
		taskProgressForm.addChangeListener(e->{
			loadData();
			checkChange = true;
			dialogTemplate.close();
		});
		dialogTemplate.add(taskProgressForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setWidth("40%");
		dialogTemplate.setHeight("80%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
		
	}
	
	private void openDialogViewAttachment(List<String> listIdAttachments) {
		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH ĐÍNH KÈM");
		
		ActtachmentForm attActtachmentForm = new ActtachmentForm(listIdAttachments, true);
		dialogTemplate.getFooter().removeAll();
	
		dialogTemplate.add(attActtachmentForm);
		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("40%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
		
	}
	
	private void openDialogViewHistory() {
		DialogTemplate dialogTemplate = new DialogTemplate("Lịch sử tiến độ");
		TaskProgressHistoryForm taskProgressHistoryForm = new TaskProgressHistoryForm(idTask);
		dialogTemplate.add(taskProgressHistoryForm);
		
		dialogTemplate.setWidth("70%");
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
		
	}
	
	public Boolean isCheck() {
		return this.checkChange;
	}


	public ButtonTemplate getBtnUpdateProgress() {
		return btnUpdateProgress;
	}


	public void setBtnUpdateProgress(ButtonTemplate btnUpdateProgress) {
		this.btnUpdateProgress = btnUpdateProgress;
	}
	
	

}

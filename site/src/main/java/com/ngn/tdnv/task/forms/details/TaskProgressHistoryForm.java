package com.ngn.tdnv.task.forms.details;

import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.models.TaskOutputModel;
import com.ngn.tdnv.task.models.TaskProcessModel;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexWrap;

public class TaskProgressHistoryForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout vLayout = new VerticalLayout();
	
	private TaskOutputModel taskOutputModel;
	private String idTask;
	private boolean isUiMobile = false;
	public TaskProgressHistoryForm(String idTask) {
		this.idTask = idTask;
		checkUIMobile();
		loadData();
		buildLayout();
		configComponent();
		createLayoutHistory();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(new H1("*Danh sách lịch sử tiến độ đã cập nhật"));
		this.add(vLayout);
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		ApiResultResponse<ApiOutputTaskModel> data = ApiTaskService.getAtask(idTask);
		if(data.isSuccess()) {
			taskOutputModel = new TaskOutputModel(data.getResult());
		}
	}
	
	private void createLayoutHistory() {
		vLayout.removeAll();
		if(taskOutputModel != null && taskOutputModel.getProcesses() != null) {
			taskOutputModel.getProcesses().forEach(model->{
				vLayout.add(createLayoutProcess(model));
			});
		}
	}
	
	private VerticalLayout createLayoutProcess(TaskProcessModel taskProcessModel) {
		VerticalLayout vLayoutProcess = new VerticalLayout();
		
		ProgressBar progressBar = new ProgressBar();
		progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
		progressBar.setValue((float)taskProcessModel.getPercent()/100);
		
		NativeLabel pNativeLabel = new NativeLabel("Tiến độ ngày "+taskProcessModel.getCreateTimeText());
		pNativeLabel.setId("pblabel");
		pNativeLabel.getStyle().setColor("#0079d6").setFontSize("20px");
		
		progressBar.getElement().setAttribute("aria-labelledby", "pblabel");
		Span progressBarLabelValue = new Span(taskProcessModel.getPercent()+"%");
		progressBarLabelValue.getStyle().setColor("#0079d6");
		HorizontalLayout progressBarLabel = new HorizontalLayout(pNativeLabel, progressBarLabelValue);
		progressBarLabel.setJustifyContentMode(JustifyContentMode.BETWEEN);
		
		ButtonTemplate btnAttachment = new ButtonTemplate("Tệp đính kèm ("+taskProcessModel.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		
		btnAttachment.getStyle().setMarginLeft("auto");
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(taskProcessModel.getAttachments());
		});
		
		Div div = new Div();
		div.add(progressBarLabel,progressBar,createLayoutKeyAndValue("Diễn giải: ", taskProcessModel.getExplainText(), null));
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setWidthFull();
		horizontalLayout.add(createLayoutKeyAndValue("Đơn vị cập nhật: ", taskProcessModel.getCreator() == null ? "Chưa cập nhật" 
				: taskProcessModel.getCreator().getOrganizationName() + " (" + taskProcessModel.getCreator().getOrganizationUserName() +")", null),
				createLayoutKeyAndValue("Ngày nhập: ", taskProcessModel.getCreateTimeText(), null),btnAttachment);
		
		if(isUiMobile) {
			horizontalLayout.getStyle().setDisplay(Display.FLEX).setFlexWrap(FlexWrap.WRAP).set("gap", "50px");
		}
		
		div.add(horizontalLayout);
		div.setWidthFull();
		
		vLayoutProcess.add(div);
		vLayoutProcess.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("10px").setBorderRadius("10px");
		
		return vLayoutProcess;
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
	
	private Component createLayoutKeyAndValue(String key,String value,String widthHeader) {
		HorizontalLayout hlayout = new HorizontalLayout();

		Span spHeader = new Span(key);
		spHeader.setWidth(widthHeader);
		spHeader.getStyle().setFontWeight(600).setFlexShrink("0");

		Span spValue = new Span(value);

		hlayout.setWidthFull();
		hlayout.add(spHeader,spValue);
		return hlayout;
	}

}

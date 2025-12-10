package com.ngn.tdnv.task.forms.details;

import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.models.TaskOutputRefuseModel;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TaskViewReasonRefuseForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	
	private TaskOutputRefuseModel taskOutputRefuseModel;
	public TaskViewReasonRefuseForm(TaskOutputRefuseModel taskOutputRefuseModel) {
		this.taskOutputRefuseModel = taskOutputRefuseModel;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		createLayout();
	}

	@Override
	public void configComponent() {
		
	}
	
	private void createLayout() {
		vLayout.removeAll();
		vLayout.setWidthFull();
		vLayout.add(createLayoutKeyAndValue("Thời gian:", taskOutputRefuseModel.getCreatedTimeText()),
				createLayoutKeyAndValue("Đơn vị thực hiện: ", taskOutputRefuseModel.getCreator().getOrganizationName()+" ("+taskOutputRefuseModel.getCreator().getOrganizationUserName()+")"),
				createLayoutKeyAndValue("Lý do: ", taskOutputRefuseModel.getReasonRefuse()));
		ActtachmentForm acttachmentForm = new ActtachmentForm(taskOutputRefuseModel.getAttachments(),true);
		acttachmentForm.getStyle().setPadding("0");
		acttachmentForm.setHeight("auto");
		vLayout.add(new Hr(),acttachmentForm);
	}
	
	private Component createLayoutKeyAndValue(String header,String content) {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);
		spanHeader.setMinWidth("110px");
		
		Span spanContent = new Span(content);
		
		
		hLayout.setWidthFull();
		hLayout.add(spanHeader,spanContent);
		
		
		return hLayout;
	}

}

package com.ngn.tdnv.task.forms.details;

import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.models.TaskCompletedModel;
import com.ngn.utils.components.ActtachmentForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TaskViewCompletedForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private VerticalLayout vLayout = new VerticalLayout();
	
	
	private TaskCompletedModel taskCompletedModel;
	public TaskViewCompletedForm(TaskCompletedModel taskCompletedModel) {
		this.taskCompletedModel = taskCompletedModel;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		this.getStyle().setBackground("rgb(237 255 238)").setPadding("5px").setBorderRadius("10px").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
		createLayout();
	}

	@Override
	public void configComponent() {
		
	}
	
	private void createLayout() {
		vLayout.removeAll();
		H4 header = new H4("*NHIỆM VỤ ĐÃ HOÀN THÀNH");
		header.getStyle().setColor("rgb(11 98 6)").setWidth("100%").setBorderBottom("1px solid #b0bbc7").setPaddingBottom("14px");;
		
		vLayout.add(header,createLayoutKeyValue("Ngày hoàn thành: ", taskCompletedModel.getCompleteTimeText(), null),
				createLayoutKeyValue("Thông tin đơn vị: ", taskCompletedModel.getCreator().getTextDisplay(), null));
		ButtonTemplate btnAttachments = new ButtonTemplate("Đính kèm ("+taskCompletedModel.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachments.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		List<String> listAttachments = new ArrayList<String>();
		taskCompletedModel.getAttachments().forEach(model->{
			listAttachments.add(model.toString());
		});
		
		btnAttachments.addClickListener(e->{
			openDialogViewAttachment(listAttachments);
		});
		
		
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.setWidthFull();
		hLayoutButton.getStyle().setBorderTop("1px solid #0000001a").setMarginTop("auto");
		
		hLayoutButton.add(btnAttachments);
		
		vLayout.setSizeFull();
		vLayout.add(hLayoutButton);
		
	}
	
	private void openDialogViewAttachment(List<String> listAttachment) {
		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH ĐÍNH KÈM");
		ActtachmentForm acttachmentForm = new ActtachmentForm(listAttachment, true);
		dialogTemplate.add(acttachmentForm);
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeightFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}
	
	
	private Component createLayoutKeyValue(String header,String content,String style) {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setSpacing(false);

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);
		spanHeader.setWidth("130px");

		Span spanContent = new Span(content);
		spanContent.getStyle().setPaddingLeft("5px");
		
		if(style != null) {
			spanContent.getElement().getThemeList().add(style);
		}

		hLayout.add(spanHeader,spanContent);

		hLayout.setSpacing(false);
		return hLayout;
	}

}

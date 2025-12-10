package com.ngn.tdnv.task.forms.details;

import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.tasks.actions.ApiPedingModel;
import com.ngn.interfaces.FormInterface;
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

public class TaskViewPenddingForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	
	private ApiPedingModel apiPedingModel;
	public TaskViewPenddingForm(ApiPedingModel apiPedingModel) {
		this.apiPedingModel = apiPedingModel;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		setupLayout();
	}

	@Override
	public void configComponent() {
		
	}
	
	private void setupLayout() {
		vLayout.removeAll();
		H4 spTitle = new H4("*NHIỆM VỤ ĐANG TẠM HOÃN.");
		spTitle.getStyle().setColor("#5a5a00").setWidth("100%").setBorderBottom("1px solid #b0bbc7").setPaddingBottom("14px");;
		
		ButtonTemplate btnAttachment = new ButtonTemplate("Đính kèm ("+apiPedingModel.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.getStyle().setMarginLeft("auto");
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(apiPedingModel.getAttachments());
		});
		
		vLayout.getStyle().setBackground("#fffaed").setPadding("5px").setBorderRadius("10px").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
		
		vLayout.add(spTitle,createLayoutKeyValue("Đơn vị tạm hoãn: ", apiPedingModel.getCreator().getOrganizationName(), null),
				createLayoutKeyValue("Người thực hiện: ", apiPedingModel.getCreator().getUserNameText(), null),
				createLayoutKeyValue("Nội dung tạm hoãn: ", apiPedingModel.getReasonPending(), null),
				createLayoutKeyValue("Thời gian: ", apiPedingModel.getCreatedTimeText(), null));
		
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.setWidthFull();
		hLayoutButton.getStyle().setBorderTop("1px solid #0000001a").setMarginTop("auto");
		
		hLayoutButton.add(btnAttachment);
		
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

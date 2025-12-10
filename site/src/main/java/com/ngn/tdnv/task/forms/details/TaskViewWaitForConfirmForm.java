package com.ngn.tdnv.task.forms.details;

import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.tasks.ApiTaskReportedModel;
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

public class TaskViewWaitForConfirmForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private VerticalLayout vLayout = new VerticalLayout();
	
	private ApiTaskReportedModel taskReport;
	public TaskViewWaitForConfirmForm(ApiTaskReportedModel taskReport) {
		this.taskReport = taskReport;
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		
		this.getStyle().setBackground("#edf4ff").setPadding("5px").setBorderRadius("10px").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
		
		createLayout();
	}

	@Override
	public void configComponent() {
		
	}
	
	private void createLayout() {
		vLayout.removeAll();
		
		H4 header = new H4("*NHIỆM VỤ ĐANG CHỜ XÁC NHẬN BÁO CÁO HOÀN THÀNH");
		header.getStyle().setColor("hsl(214deg 83.99% 43.65%)").setWidth("100%").setBorderBottom("1px solid #b0bbc7").setPaddingBottom("14px");
		
		vLayout.add(header,createLayoutKeyValue("Ngày báo cáo: ", taskReport.getCompletedTimeText(), null),
				createLayoutKeyValue("Đơn vị báo cáo: ", taskReport.getCreator() == null ? "Đang kiểm tra" : taskReport.getCreator().getOrganizationName()  , null),
				createLayoutKeyValue("Người thực hiện: ", taskReport.getCreator() == null ? "Đang kiểm tra" : taskReport.getCreator().getOrganizationUserName(), null));
		
		ButtonTemplate btnAttachment = new ButtonTemplate("Đính kèm("+taskReport.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(taskReport.getAttachments());
		});
		
		
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

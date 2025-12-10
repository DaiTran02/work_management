package com.ngn.tdnv.task.forms.details;

import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.tasks.ApiTaskRefuseConfirmModel;
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

public class TaskViewRefuseConfirmForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private VerticalLayout vLayout = new VerticalLayout();
	
	private ApiTaskRefuseConfirmModel taskRefuseConfirmModel;
	private List<ApiTaskRefuseConfirmModel> historyRefuseConfirm;
	public TaskViewRefuseConfirmForm(ApiTaskRefuseConfirmModel taskRefuseConfirmModel,List<ApiTaskRefuseConfirmModel> historyRefuseConfirm) {
		this.taskRefuseConfirmModel = taskRefuseConfirmModel;
		this.historyRefuseConfirm = historyRefuseConfirm;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
		this.getStyle().setBackground("rgb(255 244 237)").setPadding("5px").setBorderRadius("10px").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");;
		createLayout();
	}

	@Override
	public void configComponent() {
		
	}
	
	private void createLayout() {
		vLayout.removeAll();
		H4 header = new H4("*ĐƠN VỊ GIAO NHIỆM VỤ ĐÃ TỪ CHỐI BÁO CÁO");
		header.getStyle().setColor("rgb(205 37 18)").setWidth("100%").setBorderBottom("1px solid #b0bbc7").setPaddingBottom("14px");;
		vLayout.add(header);
		vLayout.add(createLayoutKeyValue("Ngày từ chối: ", taskRefuseConfirmModel.getCreateTimeText(), null),
				createLayoutKeyValue("Lý do: ", taskRefuseConfirmModel.getReasonConfirmRefuse(), null),
				createLayoutKeyValue("Đơn vị thực hiện: ", taskRefuseConfirmModel.getCreator().getOrganizationName(), null),
				createLayoutKeyValue("Người thực hiện: ", taskRefuseConfirmModel.getCreator().getOrganizationUserName(), null));
		
		ButtonTemplate btnAttachment = new ButtonTemplate("Đính kèm ("+taskRefuseConfirmModel.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(taskRefuseConfirmModel.getAttachments());
		});
		
		ButtonTemplate btnHistory = new ButtonTemplate("Lịch sử từ chối",FontAwesome.Solid.HISTORY.create());
		btnHistory.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		if(historyRefuseConfirm == null || historyRefuseConfirm.isEmpty()) {
			btnHistory.setEnabled(false);
		}
		btnHistory.addClickListener(e->openDialogViewHistory());
		
		
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.setWidthFull();
		hLayoutButton.getStyle().setBorderTop("1px solid #0000001a").setMarginTop("auto");
		
		hLayoutButton.add(btnAttachment,btnHistory);
		
		vLayout.setSizeFull();
		vLayout.add(hLayoutButton);
		
	}
	
	private void openDialogViewHistory() {
		DialogTemplate dialogTemplate = new DialogTemplate("Lịch sử từ chối");
		
		TaskViewRefuseConfirmHistoryForm taskViewRefuseConfirmHistoryForm = new TaskViewRefuseConfirmHistoryForm(historyRefuseConfirm);
		dialogTemplate.add(taskViewRefuseConfirmHistoryForm);
		
		dialogTemplate.setWidth("70%");
		dialogTemplate.setHeight("80%");
		
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
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

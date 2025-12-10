package com.ngn.tdnv.task.forms.details;

import java.util.Collections;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.models.TaskRemindModel;
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

public class TaskViewRemindForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vMain = new VerticalLayout();
	
	private List<TaskRemindModel> listReminds;
	public TaskViewRemindForm(List<TaskRemindModel> listReminds) {
		this.listReminds = listReminds;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.getStyle().setBackground("rgb(255 246 246)").setPadding("5px").setBorderRadius("10px").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");;
		this.add(vMain);
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		
		Collections.reverse(listReminds);
		
		if(listReminds != null && !listReminds.isEmpty()) {
			createLayout(listReminds.get(0));
		}
		
	}
	
	private void createLayout(TaskRemindModel taskRemindModel) {
		vMain.removeAll();
		
		ButtonTemplate btnViewHistory = new ButtonTemplate("Lịch sử nhắc nhở",FontAwesome.Solid.HISTORY.create());
		btnViewHistory.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnViewHistory.addClickListener(e->openDialogViewHistoryRemid());
		
		ButtonTemplate btnAttachment = new ButtonTemplate("Đính kèm ("+taskRemindModel.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.getStyle().setMarginLeft("auto");
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(taskRemindModel.getAttachments());
		});
		
		
		
		H4 spCount = new H4("*NHIỆM VỤ CÓ NHẮC NHỞ TỪ ĐƠN VỊ GIAO");
		spCount.getStyle().setColor("#680303").setWidth("100%").setBorderBottom("1px solid #b0bbc7").setPaddingBottom("14px");
		
		vMain.add(spCount,createLayoutKeyValue("Đơn vị nhắc nhở: ", taskRemindModel.getCreator().getOrganizationName(), null),
				createLayoutKeyValue("Lãnh đạo thực hiện: ", taskRemindModel.getCreator().getOrganizationUserName(), null),
				createLayoutKeyValue("Nội dung nhắc nhở: ", taskRemindModel.getReasonRemind(), null),
				createLayoutKeyValue("Thời gian: ", taskRemindModel.getCreateTimeText(),null));
		
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.setWidthFull();
		hLayoutButton.getStyle().setBorderTop("1px solid #0000001a").setMarginTop("auto");
		
		hLayoutButton.add(btnViewHistory,btnAttachment);
		
		vMain.setSizeFull();
		vMain.add(hLayoutButton);
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
	
	private void openDialogViewHistoryRemid() {
		DialogTemplate dialogTemplate = new DialogTemplate("Lịch sử nhắc nhở");
		
		TaskViewRemindHistoryForm taskViewRemindHistoryForm = new TaskViewRemindHistoryForm(listReminds);
		dialogTemplate.add(taskViewRemindHistoryForm);
		
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeightFull();
		
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
		
	}

}

































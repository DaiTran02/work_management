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
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TaskViewRemindHistoryForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private int count = 1;
	private VerticalLayout vMain = new VerticalLayout();
	
	private List<TaskRemindModel> listReminds;
	public TaskViewRemindHistoryForm(List<TaskRemindModel> listReminds) {
		this.listReminds = listReminds;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vMain);
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		
		Collections.reverse(listReminds);
		
		listReminds.stream().forEach(model->{
			vMain.add(createLayout(model));
		});
		
	}
	
	private Component createLayout(TaskRemindModel taskRemindModel) {
		VerticalLayout vLayout = new VerticalLayout();
		
		ButtonTemplate btnViewHistory = new ButtonTemplate("Lịch sử nhắc nhở",FontAwesome.Solid.HISTORY.create());
		btnViewHistory.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		ButtonTemplate btnAttachment = new ButtonTemplate("Đính kèm ("+taskRemindModel.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.getStyle().setMarginLeft("auto");
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(taskRemindModel.getAttachments());
		});
		
		
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.add(btnViewHistory,btnAttachment);
		
		Span spCount = new Span("*Nhắc nhở lần ("+count+")");
		spCount.getStyle().setFontWeight(600);
		spCount.getStyle().setColor("#680303").setFontSize("17px");
		count++;
		
		vLayout.add(spCount,createLayoutKeyValue("Đơn vị nhắc nhở: ", taskRemindModel.getCreator().getOrganizationName(), null),
				createLayoutKeyValue("Lãnh đạo thực hiện: ", taskRemindModel.getCreator().getOrganizationUserName(), null),
				createLayoutKeyValue("Nội dung nhắc nhở: ", taskRemindModel.getReasonRemind(), null),
				createLayoutKeyValue("Thời gian: ", taskRemindModel.getCreateTimeText(),null),new Hr(),hLayoutButton);
		
		
		vLayout.getStyle().setPadding("10px").setBorderRadius("10px").setBackground("rgb(255 246 246)");
		
		vLayout.setWidthFull();
		
		
		
		return vLayout;
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

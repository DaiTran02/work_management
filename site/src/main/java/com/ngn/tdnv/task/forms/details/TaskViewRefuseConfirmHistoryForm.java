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

public class TaskViewRefuseConfirmHistoryForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private VerticalLayout vLayout = new VerticalLayout();
	
	private List<ApiTaskRefuseConfirmModel> listTaskRefuseConfirm;
	public TaskViewRefuseConfirmHistoryForm(List<ApiTaskRefuseConfirmModel> listTaskRefuseConfirm) {
		this.listTaskRefuseConfirm = listTaskRefuseConfirm;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		vLayout.removeAll();
		
		listTaskRefuseConfirm.forEach(model->{
			vLayout.add(createLayout(model));
		});
	}
	
	private Component createLayout(ApiTaskRefuseConfirmModel taskRefuseConfirmModel) {
		VerticalLayout vLayoutItem = new VerticalLayout();
		H4 header = new H4("*Đơn vị giao từ chối báo cáo");
		header.getStyle().setColor("rgb(205 37 18)");
		vLayoutItem.add(header);
		vLayoutItem.add(createLayoutKeyValue("Ngày từ chối: ", taskRefuseConfirmModel.getCreateTimeText(), null),
				createLayoutKeyValue("Lý do: ", taskRefuseConfirmModel.getReasonConfirmRefuse(), null),
				createLayoutKeyValue("Đơn vị thực hiện: ", taskRefuseConfirmModel.getCreator().getOrganizationName(), null),
				createLayoutKeyValue("Người thực hiện: ", taskRefuseConfirmModel.getCreator().getOrganizationUserName(), null));
		
		ButtonTemplate btnAttachment = new ButtonTemplate("Đính kèm ("+taskRefuseConfirmModel.getAttachments().size()+")",FontAwesome.Solid.PAPERCLIP.create());
		btnAttachment.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAttachment.addClickListener(e->{
			openDialogViewAttachment(taskRefuseConfirmModel.getAttachments());
		});
		
		vLayoutItem.add(btnAttachment);
		
		vLayoutItem.getStyle().setBoxShadow("rgba(50, 50, 93, 0.25) 0px 2px 5px -1px, rgba(0, 0, 0, 0.3) 0px 1px 3px -1px");
		
		return vLayoutItem;
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

package com.ngn.tdnv.task.forms.details;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome.Solid.Icon;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.task.models.TaskRateModel;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TaskViewRatingForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout vMainLayout = new VerticalLayout();
	private TaskRateModel taskRateModel;
	public TaskViewRatingForm(TaskRateModel taskRateModel) {
		this.taskRateModel = taskRateModel;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vMainLayout);
		vMainLayout.getStyle().setBackground("rgb(156 212 223 / 11%)").setPadding("5px").setBorderRadius("10px").setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");;
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		vMainLayout.add(createLayout(taskRateModel));
	}
	
	private Component createLayout(TaskRateModel taskRateModel) {
		VerticalLayout vLayout = new VerticalLayout();
		
		H4 header = new H4("*THÔNG TIN ĐÁNH GIÁ");
		header.getStyle().setColor("rgb(4 73 106)").setWidth("100%").setBorderBottom("1px solid #b0bbc7").setPaddingBottom("10px");;
		
		HorizontalLayout hLayout1 = new HorizontalLayout();
		HorizontalLayout hLayoutStar = new HorizontalLayout();
		
		for(int i = 1;i <= taskRateModel.getStar();i++) {
			Icon icon = FontAwesome.Solid.STAR.create();
			icon.getStyle().setColor("#ffce44");
			hLayoutStar.add(icon);
		}
		Span spStar = new Span("Đánh giá: ");
		spStar.getStyle().setFontWeight(600);
		hLayout1.add(spStar,hLayoutStar);
		
		
		vLayout.add(header,hLayout1,createLayoutKeyValue("Nhận xét: ", taskRateModel.getExplain(), null),
				createLayoutKeyValue("Người thực hiện: ", taskRateModel.getCreator().getOrganizationUserName(), null),
				createLayoutKeyValue("Từ đơn vị: ", taskRateModel.getCreator().getOrganizationName(), null),
				createLayoutKeyValue("Thời gian: ", taskRateModel.getCreatedTimeText(), null));
		
		return vLayout;
	}
	
	private Component createLayoutKeyValue(String header,String content,String style) {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setSpacing(false);
		hLayout.getStyle().set("gap", "10px");

		Span spanHeader = new Span(header);
		spanHeader.getStyle().setFontWeight(600);
		spanHeader.setWidth("110px");

		Span spanContent = new Span(content);

		if(style != null) {
			spanContent.getElement().getThemeList().add(style);
		}

		hLayout.add(spanHeader,spanContent);

		hLayout.setSpacing(false);
		return hLayout;
	}

}

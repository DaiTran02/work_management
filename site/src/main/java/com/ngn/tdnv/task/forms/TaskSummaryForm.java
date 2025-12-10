package com.ngn.tdnv.task.forms;

import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.ContentAlignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.TextAlign;

public class TaskSummaryForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout vLayout = new VerticalLayout();
	private ButtonTemplate btnHeader;
	private Span countHeader;
	private ButtonTemplate[]  buttons;
	public TaskSummaryForm(ButtonTemplate btnHeader,Span countHeader) {
		this.btnHeader = btnHeader;
		this.countHeader = countHeader;
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.add(vLayout);
		this.getStyle().setBoxShadow("rgba(99, 99, 99, 0.2) 0px 2px 8px 0px");
		this.setSpacing(false);
		createLayout();
		
	}

	@Override
	public void configComponent() {
		
	}
	
	public void addMoreButton(ButtonTemplate...buttonTemplates) {
		if(buttonTemplates != null) {
			buttons = buttonTemplates;
		}
		createLayout();
	}
	
	
	private void createLayout() {
		vLayout.removeAll();
		HorizontalLayout hLayoutHeader = new HorizontalLayout();
		// The className in file task.css
		countHeader.addClassName("task__card_overview");
		hLayoutHeader.add(btnHeader,countHeader);
		hLayoutHeader.setWidthFull();
		hLayoutHeader.setPadding(false);
		hLayoutHeader.setHeight("22px");
		
		FlexLayout flexLayout = new FlexLayout();
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.setFlexDirection(FlexDirection.ROW);
		flexLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		flexLayout.setAlignContent(ContentAlignment.SPACE_BETWEEN);
		
		HorizontalLayout hLayoutContent = new HorizontalLayout();
		VerticalLayout vLayout1 = new VerticalLayout();
		vLayout1.setWidth("50%");
		vLayout1.setPadding(false);
		vLayout1.setSpacing(false);
		
		VerticalLayout vLayout2 = new VerticalLayout();
		vLayout2.setWidth("50%");
		vLayout2.setPadding(false);
		vLayout2.setSpacing(false);
		
		if(buttons!=null) {
			for(ButtonTemplate button : buttons) {
				button.getStyle().set("word-wrap", "inherit").setTextAlign(TextAlign.LEFT);
				if(vLayout1.getComponentCount() < 2) {
					vLayout1.add(button);
				}else {
					vLayout2.add(button);
				}
			}
		}
		
		hLayoutContent.setWidthFull();
		hLayoutContent.setSpacing(false);
		hLayoutContent.add(vLayout1,vLayout2);
		hLayoutContent.getStyle().setMarginTop("10px");
		
		vLayout.setSpacing(false);
		
		vLayout.add(hLayoutHeader,hLayoutContent);
	}

}



















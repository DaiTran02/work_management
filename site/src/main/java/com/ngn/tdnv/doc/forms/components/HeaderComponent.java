package com.ngn.tdnv.doc.forms.components;

import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class HeaderComponent extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private VerticalLayout vLayout = new VerticalLayout();

	private String nameHeader;
	public HeaderComponent(String nameHeader) {
		this.nameHeader = nameHeader;
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.setWidthFull();
		this.add(vLayout);
		H4 header = new H4(nameHeader);
		header.setWidthFull();
		vLayout.add(header,new Hr());
		vLayout.setWidthFull();
//		vLayout.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px");
	}

	@Override
	public void configComponent() {
		
	}
	
	public void addLayout(Component component) {
		vLayout.add(component);
	}
	
	public void removeAllLayout() {
		vLayout.removeAll();
	}
}

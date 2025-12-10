package com.ngn.utils.commons;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;

public abstract class TabTemplate extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	protected TabSheet tabs = new TabSheet();
	
	protected void addTab(Component title,Component content) {
		tabs.add(title, content);
	}
	
	public TabTemplate() {
		builLayout();
	}
	
	private void builLayout() {
		this.setSizeFull();
		tabs.setSizeFull();
		this.add(tabs);
	}

}

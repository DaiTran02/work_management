package com.ngn.setting;

import com.ngn.interfaces.FormInterface;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;

public class SettingView extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private TabSheet tabSheet;
	
	public SettingView() {
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createLayout());
	}

	@Override
	public void configComponent() {
		
	}
	
	private Component createLayout() {
		tabSheet = new TabSheet();
		tabSheet.setSizeFull();
		
		tabSheet.add("Cấu hình", new Span("ABC"));
		tabSheet.add("Nhật ký đăng nhập", new Span("ABC"));
		
		//use file css tab.css for this classname
		tabSheet.addClassNames("tab_setting");
		
		
		return tabSheet;
	}

}

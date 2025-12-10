package com.ngn.utils.components;

import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;

public class MenuBarTemplate extends MenuBar{
	private static final long serialVersionUID = 1L;
	
	public MenuBarTemplate() {
		this.getStyle().setCursor("pointer");
		this.getItems().forEach(item->{
			item.getStyle().setCursor("pointer");
		});
		this.addThemeVariants(MenuBarVariant.LUMO_SMALL);
	}

}

package com.ngn.utils.commons;

import com.ngn.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
public class MainLayoutUtil {
	public static MainLayout getMainLayout() {
		return (MainLayout)UI.getCurrent().getChildren().filter(component -> component.getClass() == MainLayout.class).findFirst().orElse(null);
	}
}

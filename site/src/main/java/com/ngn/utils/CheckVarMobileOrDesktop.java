package com.ngn.utils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

public class CheckVarMobileOrDesktop extends Div{
	private static final long serialVersionUID = 1L;

	public CheckVarMobileOrDesktop() {
		try {
			System.out.println(UI.getCurrent().getSession().getBrowser().getBrowserApplication());
		} catch (Exception e) {
		}
	}

}

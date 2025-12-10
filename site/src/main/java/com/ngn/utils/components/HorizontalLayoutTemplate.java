package com.ngn.utils.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;

public class HorizontalLayoutTemplate extends HorizontalLayout{
	private static final long serialVersionUID = 1L;
	
	public HorizontalLayoutTemplate() {
		
	}
	
	public Registration addChangeListener(ComponentEventListener<ClickEvent> listener) {
		return addListener(ClickEvent.class, listener);
	}

	public static class ClickEvent extends ComponentEvent<HorizontalLayoutTemplate> {
		private static final long serialVersionUID = 1L;

		public ClickEvent(HorizontalLayoutTemplate source, boolean fromClient) {
			super(source, fromClient);
		}
	}

}

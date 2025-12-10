package com.ngn.utils.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.shared.Registration;

public class ComboboxTemplate extends ComboBox<Object>{
	private static final long serialVersionUID = 1L;
	
	public ComboboxTemplate(String name) {
		super();
		this.setLabel(name);
	}
	
	public Registration addChangeListener(ComponentEventListener<ClickEvent> listener) {
		return addListener(ClickEvent.class, listener);
	}

	public static class ClickEvent extends ComponentEvent<ComboboxTemplate> {
		private static final long serialVersionUID = 1L;

		public ClickEvent(ComboboxTemplate source, boolean fromClient) {
			super(source, fromClient);
		}
	}

}

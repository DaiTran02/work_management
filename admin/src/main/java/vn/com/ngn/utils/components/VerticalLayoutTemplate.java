package vn.com.ngn.utils.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class VerticalLayoutTemplate extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	public VerticalLayoutTemplate() {
		
	}
	
	public Registration addChangeListener(ComponentEventListener<ClickEvent> listener) {
		return addListener(ClickEvent.class, listener);
	}

	public static class ClickEvent extends ComponentEvent<VerticalLayoutTemplate> {
		private static final long serialVersionUID = 1L;

		public ClickEvent(VerticalLayoutTemplate source, boolean fromClient) {
			super(source, fromClient);
		}
	}
}

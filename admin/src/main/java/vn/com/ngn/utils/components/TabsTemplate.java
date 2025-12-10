package vn.com.ngn.utils.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.shared.Registration;

public abstract class TabsTemplate extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	protected TabSheet tabSheet = new TabSheet();
	
	protected void addTab(Component title,Component content) {
		tabSheet.add(title, content);
	}
	
	public TabsTemplate() {
		buildLayout();
	}
	
	private void buildLayout() {
		this.setSizeFull();
		tabSheet.setSizeFull();
		this.add(tabSheet);
	}
	
	public Registration addChangeListener(ComponentEventListener<ClickEvent> listener) {
		return addListener(ClickEvent.class, listener);
	}

	public static class ClickEvent extends ComponentEvent<TabsTemplate> {
		private static final long serialVersionUID = 1L;

		public ClickEvent(TabsTemplate source, boolean fromClient) {
			super(source, fromClient);
		}
	}
}

package com.ngn.tdnv.task.forms.components;

import com.ngn.utils.components.HorizontalLayoutTemplate;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.dom.Style.AlignItems;

public class ChipComponentStatus extends HorizontalLayoutTemplate{
	private static final long serialVersionUID = 1L;
	
	public ChipComponentStatus(String title, String color) {
		Div div = new Div();
		div.getStyle().setWidth("1px").setHeight("1px").setBackground(color).setBorder("5px solid "+color);
		Span spTitle = new Span(title);
		
		this.getStyle().setAlignItems(AlignItems.CENTER).setCursor("pointer");
		this.addClickListener(e->{
			fireEvent(new ClickEvent(this, false));
		});
		
		this.add(div,spTitle);
	}

}

package com.ngn.utils.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;

public class DetailsTemplate extends Details{
	private static final long serialVersionUID = 1L;
	
	public DetailsTemplate() {
		builLayout();
	}
	
	public DetailsTemplate(String title) {
		this.setSummaryText(title);
		builLayout();
	}
	
	public DetailsTemplate(String title,Component icon) {
		Button btnTitle = new Button(title,icon);
		btnTitle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		this.setSummary(btnTitle);
		builLayout();
	}
	
	public DetailsTemplate(Checkbox checkBox) {
		this.setSummary(checkBox);
		checkBox.addClickListener(e->{
			
		});
		builLayout();
	}
	
	private void builLayout() {
		this.setWidthFull();
		this.setOpened(true);
		this.addThemeVariants(DetailsVariant.REVERSE,DetailsVariant.FILLED);
	}

}

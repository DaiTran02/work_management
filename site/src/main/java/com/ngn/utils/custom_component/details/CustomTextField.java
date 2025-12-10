package com.ngn.utils.custom_component.details;

import com.ngn.utils.custom_component.CustomComponentModel;
import com.ngn.utils.custom_component.CustomComponentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;

public class CustomTextField implements CustomComponentService{

	@Override
	public Component createComponent(CustomComponentModel customComponentModel) {
		TextField txt = new TextField();
		return txt;
	}

}

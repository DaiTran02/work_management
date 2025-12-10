package com.ngn.utils.custom_component.details;

import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.custom_component.CustomComponentModel;
import com.ngn.utils.custom_component.CustomComponentService;
import com.vaadin.flow.component.Component;

public class CustomButton implements CustomComponentService{

	@Override
	public Component createComponent(CustomComponentModel customComponentModel) {
		ButtonTemplate button = new ButtonTemplate();
		return button;
	}

}

package com.ngn.utils.custom_component.details;

import org.apache.commons.lang3.tuple.Pair;

import com.ngn.utils.custom_component.CustomComponentModel;
import com.ngn.utils.custom_component.CustomComponentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.select.Select;

public class CustomSelect implements CustomComponentService{

	@Override
	public Component createComponent(CustomComponentModel customComponentModel) {
		Select<Pair<String, String>> select = new Select<Pair<String,String>>();
		return select;
	}

}

package com.ngn.utils.custom_component;

import java.util.HashMap;
import java.util.Map;

import com.ngn.utils.custom_component.details.CustomButton;
import com.ngn.utils.custom_component.details.CustomSelect;
import com.ngn.utils.custom_component.details.CustomTextField;

public class CustomComponentUtil {
	private static final Map<String, CustomComponentService> mapComponent = new HashMap<String, CustomComponentService>();
	
	static {
		mapComponent.put("textfield", new CustomTextField());
		mapComponent.put("button", new CustomButton());
		mapComponent.put("select", new CustomSelect());
	}
	
	public static CustomComponentService getComponent(String type) {
		return mapComponent.get(type);
	}

}

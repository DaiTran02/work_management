package vn.com.ngn.page.setting.model;

import com.vaadin.flow.component.Component;

import lombok.Data;

@Data
public class ComponentModel {
	private String key;
	private Component component;
	
	public ComponentModel() {
		
	}
	
	public ComponentModel(Component component,String key) {
		this.key = key;
		this.component = component;
	}

}

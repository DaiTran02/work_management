package com.ngn.utils.components.model;

import com.vaadin.flow.component.Component;

import lombok.Data;

@Data
public class GridModel {
	private Component key;
	private Component value;
	
	public String getStringValue() {
		return value.getElement().getText();
	}

}

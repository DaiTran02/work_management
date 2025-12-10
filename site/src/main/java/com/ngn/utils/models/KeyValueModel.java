package com.ngn.utils.models;

import com.ngn.api.utils.ApiKeyValueModel;

import lombok.Data;

@Data
public class KeyValueModel {
	private String key;
	private String name;
	
	public KeyValueModel() {
		
	}
	
	public KeyValueModel(ApiKeyValueModel apiKeyValueModel) {
		this.key = apiKeyValueModel.getKey();
		this.name = apiKeyValueModel.getName();
	}

}

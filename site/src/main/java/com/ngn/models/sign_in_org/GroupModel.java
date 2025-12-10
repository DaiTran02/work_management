package com.ngn.models.sign_in_org;

import com.ngn.api.sign_in_org.ApiGroupModel;

import lombok.Data;

@Data
public class GroupModel {
	private String id;
	private String name;
	private String description;
	
	public GroupModel() {
		
	}
	
	public GroupModel(ApiGroupModel apiGroupModel) {
		this.id = apiGroupModel.getId();
		this.name = apiGroupModel.getName();
		this.description = apiGroupModel.getDescription();
	}
}

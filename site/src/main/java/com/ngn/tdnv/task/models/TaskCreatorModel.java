package com.ngn.tdnv.task.models;

import com.ngn.api.tasks.ApiCreatorModel;

import lombok.Data;

@Data
public class TaskCreatorModel {
	private String organizationId;
	private String organizationName;
	private String organizationUserId;
	private String organizationUserName;
	
	public TaskCreatorModel() {
		
	}
	
	public TaskCreatorModel(ApiCreatorModel api) {
		this.organizationId = api.getOrganizationId();
		this.organizationName = api.getOrganizationName();
		this.organizationUserId = api.getOrganizationUserId();
		this.organizationUserName = api.getOrganizationUserName();
	}
}

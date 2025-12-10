package com.ngn.tdnv.task.models;

import org.modelmapper.ModelMapper;

import com.ngn.api.tasks.ApiFollowerModel;

import lombok.Data;

@Data
public class FollowerModel {
	private String organizationId;
	private String organizationName;
	private String organizationGroupId;
	private String organizationGroupName;
	private Object organizationUserId;
	private Object organizationUserName;
	
	public FollowerModel() {
		
	}
	
	public FollowerModel(ApiFollowerModel apiFollowerModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiFollowerModel, this);
	}
	
	public String getUser() {
		if(organizationUserName != null) {
			return organizationGroupName + "("+organizationUserName.toString()+")";
		}
		return "";
	}
	
}

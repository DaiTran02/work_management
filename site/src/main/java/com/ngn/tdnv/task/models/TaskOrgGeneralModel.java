package com.ngn.tdnv.task.models;

import org.modelmapper.ModelMapper;

import com.ngn.api.tasks.ApiOrgGeneralOfTaskModel;

import lombok.Data;

@Data
public class TaskOrgGeneralModel {
	private String organizationId;
	private String organizationName;
    private String organizationGroupId;
    private String organizationGroupName;
	private Object organizationUserId;
	private Object organizationUserName;
	private String textDisplay;
	
	public TaskOrgGeneralModel() {
		
	}
	
	public TaskOrgGeneralModel(ApiOrgGeneralOfTaskModel apiOrgGeneralOfTaskModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiOrgGeneralOfTaskModel, this);
	}
	
	public String getOrgUserName() {
		return organizationUserId == null ? "" : organizationUserName.toString();
	}
	
	public String getUserNameText() {
		return organizationUserId == null ? "Chưa phân người xử lý" : organizationUserName.toString();
	}
	
	public String getUser() {
		if(organizationUserName != null) {
			return organizationGroupName+ "("+organizationUserName.toString()+")";
		}
		return "";
	}
	
}

package com.ngn.api.tasks;

import org.modelmapper.ModelMapper;

import com.ngn.tdnv.task.models.TaskOrgGeneralModel;

import lombok.Data;

@Data
public class ApiOrgGeneralOfTaskModel {
	private String organizationId;
	private String organizationName;
	private String organizationGroupId;
	private String organizationGroupName;
	private Object organizationUserId;
	private Object organizationUserName;
	private String textDisplay;
	
	public ApiOrgGeneralOfTaskModel() {
		
	}
	
	public ApiOrgGeneralOfTaskModel(TaskOrgGeneralModel ofTaskModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(ofTaskModel, this);
	}
}

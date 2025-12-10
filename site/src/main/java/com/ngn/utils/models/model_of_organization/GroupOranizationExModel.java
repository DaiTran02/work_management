package com.ngn.utils.models.model_of_organization;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.organization.ApiGroupExpandModel;

import lombok.Data;

@Data
public class GroupOranizationExModel {
	private String groupId;
	private long createdTime;
	private long updatedTime;
	private String name;
	private String description;
	private String creatorId;
	private String creatorName;
	private List<String> userIds;
	
	public GroupOranizationExModel() {
		
	}
	
	public GroupOranizationExModel(ApiGroupExpandModel apiGroupExpandModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiGroupExpandModel, this);
	}

}

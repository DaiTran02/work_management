package com.ngn.utils.models.model_of_organization;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.organization.ApiRoleOrganizationExModel;

import lombok.Data;

@Data
public class RoleOrganizationModel {
	private String roleId;
	private Date createdTime;
	private Date updatedTime;
	private String name;
	private String description;
	private String creatorId;
	private String creatorName;
	private List<String> permissionKeys;
	private List<String> userIds;
	private long createdTimeLong;
	private long updatedTimeLong;
	
	public RoleOrganizationModel() {
		
	}
	
	public RoleOrganizationModel(ApiRoleOrganizationExModel apiRoleOrganizationExModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiRoleOrganizationExModel, this);
	}
}

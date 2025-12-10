package com.ngn.utils.models.model_of_organization;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.organization.ApiOrganizationModel;

import lombok.Data;

@Data
public class OrganizationModel {
	private String id;
	private String idNew;
	private long createdTime;
	private long updatedTime;
	private String name;
	private String description;
	private String creatorId;
	private String creatorName;
	private String path;
	private String parentId;
	private List<String> parentIdSeconds;
	private boolean active;
	private int order;
	private String unitCode;
	private int countSubOrganization;
	private String organizationCategoryId;
	private List<UserOranizationExModel> userOrganizationExpands;
	private List<GroupOranizationExModel> groupOrganizationExpands;
	private List<RoleOrganizationModel> roleOrganizationExpands;
	
	public OrganizationModel() {
		
	}
	
	public OrganizationModel(ApiOrganizationModel apiOrganizationModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiOrganizationModel, this);
	}

}

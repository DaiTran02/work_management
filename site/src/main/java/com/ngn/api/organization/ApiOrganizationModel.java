package com.ngn.api.organization;

import java.util.List;

import com.ngn.api.utils.ApiKeyValueModel;

import lombok.Data;

@Data
public class ApiOrganizationModel {
	private String id;
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
	private ApiKeyValueModel level;
	private List<ApiUserOrganizationExModel> userOrganizationExpands;
	private List<ApiGroupExpandModel> groupOrganizationExpands;
	private List<ApiRoleOrganizationExModel> roleOrganizationExpands;

}

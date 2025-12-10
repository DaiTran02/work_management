package com.ngn.api.organization;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ApiRoleOrganizationExModel {
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
}

package com.ngn.api.organization;

import lombok.Data;

@Data
public class ApiPermissionModel {
	private String id;
	private String key;
	private String name;
	private String description;
	private int orderSort;
	private String groupId;
	private String groupName;
	private int groupSort;
}

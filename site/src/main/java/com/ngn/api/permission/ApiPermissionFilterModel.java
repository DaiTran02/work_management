package com.ngn.api.permission;

import lombok.Data;

@Data
public class ApiPermissionFilterModel {
	private String userId;
	private String permissionKey;
}

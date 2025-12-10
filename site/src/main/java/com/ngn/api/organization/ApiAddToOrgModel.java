package com.ngn.api.organization;

import lombok.Data;

@Data
public class ApiAddToOrgModel {
	private String organizationId;
	private String roleId;
	private String groupId;
}

package com.ngn.api.organization;

import lombok.Data;

@Data
public class ApiFilterOrgModel {
	private int skip = 0;
	private int limit = 0;
	private String parentId;
	private String keyword;
	private String organizationCategoryId;
}

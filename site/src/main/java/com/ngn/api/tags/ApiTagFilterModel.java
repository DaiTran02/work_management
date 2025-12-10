package com.ngn.api.tags;

import lombok.Data;

@Data
public class ApiTagFilterModel {
	private String organizationId;
	private String userId;
	private int skip;
	private int limit;
	private String keyword;
	private String type;
	private boolean active;
}

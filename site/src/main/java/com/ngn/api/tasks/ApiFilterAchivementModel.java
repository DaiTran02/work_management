package com.ngn.api.tasks;

import lombok.Data;

@Data
public class ApiFilterAchivementModel {
	private int skip;
	private int limit;
	private long fromDate;
	private long toDate;
	private String ownerOrganizationId;
	private String ownerOrganizationUserId;
	private String assigneeOrganizationId;
	private String assigneeOrganizationUserId;
	private String supportOrganizationId;
	private String supportOrganizationUserId;
	private String followerOrganizationGroupId;
	private String followerOrganizationUserId;
}

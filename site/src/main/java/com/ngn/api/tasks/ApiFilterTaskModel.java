package com.ngn.api.tasks;

import lombok.Data;

@Data
public class ApiFilterTaskModel {
	private int skip = 0;
	private int limit = 10;
	private long fromDate = 0;
	private long toDate = 0;
	private String ownerOrganizationId;
	private String ownerOrganizationUserId;
	private String assigneeOrganizationId;
	private String assigneeOrganizationUserId;
	private String supportOrganizationId;
	private String supportOrganizationUserId;
	private String followerOrganizationId;
	private String followerOrganizationGroupId;
	private String followerOrganizationUserId;
	private String assistantOrganizationGroupId;
	private String assistantOrganizationUserId;
	private String status;
	private String priority;
	private String keyword;
	private String docNumber;
	private String docSymbol;
	private String source;
	private String docCategory;
	private String dataScopeType;
	private String tagIds;
	private String idAssigneeUser;
	private Boolean onlyOwner;
	private Boolean kpi;
}

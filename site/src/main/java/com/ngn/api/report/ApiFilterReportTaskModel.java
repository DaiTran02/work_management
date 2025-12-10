package com.ngn.api.report;

import lombok.Data;

@Data
public class ApiFilterReportTaskModel {
	private long fromDate;
	private long toDate;
	private int skip;
	private int limit;
	private String ownerOrganizationId;
	private String ownerOrganizationUserId;
	private String assigneeOrganizationId;
	private String assigneeOrganizationUserId;
	private String supportOrganizationId;
	private String supportOrganizationUserId;
	private String followerOrganizationId;
	private String followerOrganizationGroupId;
	private String followerOrganizationUserId;
	private String status;
	private String priority;
	private String classifyTaskId;
	private String leaderApproveTaskId;
	private String docCategory;
	private String dataScopeType;
}

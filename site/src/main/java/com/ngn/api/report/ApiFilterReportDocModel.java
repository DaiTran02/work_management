package com.ngn.api.report;

import lombok.Data;

@Data
public class ApiFilterReportDocModel {
	private long fromDate;
	private long toDate;
	private int skip;
	private int limit;
	private String status;
	private String category;
	private Object classifyTaskId;
	private Object leaderApproveTaskId;
	private String organizationId;
	private String organizationGroupId;
	private String organizationUserId;
	private String dataScopeType;
}

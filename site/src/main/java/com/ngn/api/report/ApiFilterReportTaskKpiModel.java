package com.ngn.api.report;

import lombok.Data;

@Data
public class ApiFilterReportTaskKpiModel {
	private int skip = 0;
	private int limit = 0;
	private long fromDate;
	private long toDate;
	private String dataScopeType;
	private Boolean isKpi = true;
	private String assigneeOrganizationId;
	private String assigneeOrganizationUserId;
	private String ownerOrganizationId;
	private String ownerOrganizationUserId;
	private String supportOrganizationId;
	private String supportOrganizationUserId;
	private String followerOrganizationId;
	private String followerOrganizationUserId;
}

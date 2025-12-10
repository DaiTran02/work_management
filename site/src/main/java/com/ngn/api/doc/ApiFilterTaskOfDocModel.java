package com.ngn.api.doc;

import lombok.Data;

@Data
public class ApiFilterTaskOfDocModel {
	private int skip = 0;
	private int limit = 0;
	private long fromDate = 0;
	private long toDate = 0;
	private String docId;
	private String ownerOrganizationId;
	private String ownerOrganizationUserId;
	private String assigneeOrganizationId;
	private String assigneeOrganizationUserId;
	private String supportOrganizationId;
	private String followerOrganizationGroupId;
	private String followerOrganizationUserId;
	private String status;
	private String priority;
	private String keyword;
}

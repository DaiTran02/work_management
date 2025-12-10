package com.ngn.api.doc;

import lombok.Data;

@Data
public class ApiFilterSummaryDocModel {
	private long fromDate;
	private long toDate;
	private String docId;
	private String assigneeOrganizationId;
}

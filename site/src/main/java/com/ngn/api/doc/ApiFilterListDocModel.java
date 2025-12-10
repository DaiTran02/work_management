package com.ngn.api.doc;

import lombok.Data;

@Data
public class ApiFilterListDocModel {
	private int skip;
	private int limit;
	private long fromDate = 0;
	private long toDate = 0;
	private String organizationId = null;
	private String organizationGroupId = null;
	private String organizationUserId = null;
	private String category = null;
	private String status = null;
	private String keyword = null;
	private String number;
	private String symbol;
	private boolean active;
	private String dataScopeType;
	private String tagIds;
}

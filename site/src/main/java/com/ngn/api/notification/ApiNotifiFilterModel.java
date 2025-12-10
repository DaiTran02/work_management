package com.ngn.api.notification;

import lombok.Data;

@Data
public class ApiNotifiFilterModel {
	private int skip;
	private int limit;
	private long fromDate;
	private long toDate;
	private String organizationId;
	private String organizationUserId;
}

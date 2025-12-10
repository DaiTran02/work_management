package com.ngn.api.personal_record;

import lombok.Data;

@Data
public class ApiPersonalFilter {
	private String keySearch;
	private String userId;
	private String oldUserId;
	private String transferredUserId;
}

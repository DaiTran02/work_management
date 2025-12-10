package com.ngn.api.organization;

import lombok.Data;

@Data
public class ApiUserOrganizationExModel {
	private String userId;
	private long createdTime;
	private long updatedTime;
	private String userName;
	private String fullName;
	private String positionName;
	private String accountIOffice;
	private boolean active;
	private Object status;
}

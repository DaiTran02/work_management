package com.ngn.api.organization;

import lombok.Data;

@Data
public class ApiMoreinfoModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String username;
	private String email;
	private String phone;
	private String fullName;
	private Object jobTitle;
	private String creatorId;
	private String creatorName;
	private boolean active;
	private String activeCode;
	private long lastDateLogin;
	private long lastChangePassword;
	private Object lastIPLogin;
//	private BelongOrganization belongOrganizations;

	@Data
	public class BelongOrganization {
		private String organizationId;
		private String organizationName;
		
		public BelongOrganization() {
			
		}
	}
}

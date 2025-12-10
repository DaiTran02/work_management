package com.ngn.api.tasks.actions;

import lombok.Data;

@Data
public class ApiDoAssignModel {
	private String organizationUserId;
	private String organizationUserName;
	private Creator creator;

	@Data
	public class Creator{
		private String organizationId;
		private String organizationName;
		private String organizationUserId;
		private String organizationUserName;
	}

}

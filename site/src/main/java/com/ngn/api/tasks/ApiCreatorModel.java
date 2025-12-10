package com.ngn.api.tasks;

import lombok.Data;

@Data
public class ApiCreatorModel {
	private String organizationId;
	private String organizationName;
	private String organizationUserId;
	private String organizationUserName;
	
	public String getUsernameText() {
		return organizationUserName == null ? "Đang cập nhật" : organizationUserName;
	}
}

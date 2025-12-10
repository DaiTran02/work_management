package com.ngn.api.tasks.actions;

import lombok.Data;

@Data
public class ApiCreatorActionModel {
	private String organizationId;
	private String organizationName;
	private String organizationUserId;
	private String organizationUserName;
	private Object jobTitle;
	private String textDisplay;
	
	public String getUserNameText() {
		return organizationUserName == null ? "Đang cập nhật" : organizationUserName;
	}
}

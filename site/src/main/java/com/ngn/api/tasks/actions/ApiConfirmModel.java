package com.ngn.api.tasks.actions;

import lombok.Data;

@Data
public class ApiConfirmModel {
	private long completedTime;
	private ApiCreatorActionModel creator;
}

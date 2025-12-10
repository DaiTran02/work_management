package com.ngn.api.tasks.actions;

import java.util.List;

import lombok.Data;

@Data
public class ApiCompletedInputModel {
	private long completedTime;
	private List<String> attachments;
	private ApiCreatorActionModel creator;
	private boolean ignoreRequiredConfirm;
}

package com.ngn.api.tasks.actions;

import java.util.List;

import lombok.Data;

@Data
public class ApiReportModel {
	private long completedTime;
	private List<String> attachments;
	private ApiCreatorActionModel creator;
}

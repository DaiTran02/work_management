package com.ngn.api.tasks;

import java.util.List;

import lombok.Data;

@Data
public class ApiTaskRemindModel {
	private String id;
	private long createdTime;
	private String reasonRemind;
	private ApiCreatorModel creator;
	private List<String> attachments;
}

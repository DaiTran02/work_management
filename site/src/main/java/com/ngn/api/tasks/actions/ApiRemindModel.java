package com.ngn.api.tasks.actions;

import java.util.List;

import lombok.Data;

@Data
public class ApiRemindModel {
	private String reasonRemind;
	private ApiCreatorActionModel creator;
	private List<String> attachments;
}

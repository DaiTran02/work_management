package com.ngn.api.tasks.actions;

import java.util.List;

import lombok.Data;

@Data
public class ApiReverseModel {
	private String reasonReverse;
	private List<String> attachments;
	private ApiCreatorActionModel creator;
}

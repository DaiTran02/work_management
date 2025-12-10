package com.ngn.api.tasks.actions;

import java.util.List;

import lombok.Data;

@Data
public class ApiCommentModel {
	private String message;
	private ApiCreatorActionModel creator;
	private List<String> attachments;
}

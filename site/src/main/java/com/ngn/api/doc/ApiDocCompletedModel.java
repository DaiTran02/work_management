package com.ngn.api.doc;

import java.util.List;

import com.ngn.api.tasks.ApiCreatorModel;

import lombok.Data;

@Data
public class ApiDocCompletedModel {
	private long completedTime;
	private String content;
	private List<String> attachments;
	private ApiCreatorModel creator;
}

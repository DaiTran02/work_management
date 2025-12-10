package com.ngn.api.tasks;

import java.util.List;

import lombok.Data;

@Data
public class ApiTaskRateModel {
	private String id;
	private long createdTime;
	private int star;
	private String explain;
	private List<String> attachments;
	private ApiCreatorModel creator;
}

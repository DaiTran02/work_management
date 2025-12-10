package com.ngn.api.tasks.actions;

import java.util.List;

import lombok.Data;

@Data
public class ApiUpdateProcessModel {
	private double percent;
	private String explain;
	private ApiCreatorActionModel creator;
	private List<String> attachments;
}

package com.ngn.api.tasks.actions;

import java.util.List;

import lombok.Data;

@Data
public class ApiRedoModel {
	private String reasonRedo;
	private ApiCreatorActionModel creator;
	private List<String> attachments;
}

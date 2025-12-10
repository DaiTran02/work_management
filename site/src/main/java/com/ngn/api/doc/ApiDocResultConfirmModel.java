package com.ngn.api.doc;

import java.util.List;

import com.ngn.api.tasks.ApiCreatorModel;

import lombok.Data;

@Data
public class ApiDocResultConfirmModel {
	private Long confirmedTime;
	private Long completedTime;
	private String content;
	private List<String> attachments;
	private ApiCreatorModel creator;
}

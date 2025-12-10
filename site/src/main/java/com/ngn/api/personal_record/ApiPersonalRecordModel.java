package com.ngn.api.personal_record;

import java.util.List;

import com.ngn.api.tasks.ApiCreatorModel;

import lombok.Data;

@Data
public class ApiPersonalRecordModel {
	private String id;
	private Long createdTime;
	private Long updatedTime;
	private String title;
	private String description;
	private String userId;
	private ApiPersonalUserModel currentUser;
	private Long transferTime;
	private List<ApiPersonalUserModel> oldUsers;
	private List<String> docs;
	private List<String> tasks;
	private ApiCreatorModel creator;
}

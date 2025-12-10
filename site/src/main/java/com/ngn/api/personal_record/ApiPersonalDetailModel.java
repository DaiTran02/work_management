package com.ngn.api.personal_record;

import java.util.Date;
import java.util.List;

import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.tasks.ApiCreatorModel;
import com.ngn.api.tasks.ApiOutputTaskModel;

import lombok.Data;

@Data
public class ApiPersonalDetailModel {
	private String id;
	private Date createdTime;
	private Date updatedTime;
	private String title;
	private String description;
	private String userId;
	private ApiPersonalUserModel currentUser;
	private List<ApiPersonalUserModel> oldUsers;
	private List<ApiDocModel> docs;
	private List<ApiOutputTaskModel> tasks;
	private ApiCreatorModel creator;
}

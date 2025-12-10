package com.ngn.api.tasks;

import java.util.List;

import com.ngn.api.tasks.actions.ApiCreatorActionModel;

import lombok.Data;

@Data
public class ApiProcessModel {
	  private String id;
	  private long createdTime;
	  private int percent;
	  private String explain;
	  private ApiCreatorActionModel creator;
	  private List<String> attachments;
	  private long createdTimeLong;
}

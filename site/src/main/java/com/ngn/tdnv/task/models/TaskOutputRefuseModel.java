package com.ngn.tdnv.task.models;

import java.util.List;

import com.ngn.api.tasks.ApiOutputRefuseModel;
import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class TaskOutputRefuseModel {
	private String id;
	private long createdTime;
	private String reasonRefuse;
	private List<String> attachments;
	private TaskCreatorModel creator;
	
	public TaskOutputRefuseModel() {
		
	}
	
	public TaskOutputRefuseModel(ApiOutputRefuseModel apiOutputRefuseModel) {
		this.id = apiOutputRefuseModel.getId();
		this.createdTime = apiOutputRefuseModel.getCreatedTime();
		this.reasonRefuse = apiOutputRefuseModel.getReasonRefuse();
		this.attachments.addAll(apiOutputRefuseModel.getAttachments());
		this.creator = new TaskCreatorModel(apiOutputRefuseModel.getCreator());
	}
	
	public String getCreatedTimeText() {
		return LocalDateUtil.dfDateTime.format(createdTime);
	}
}

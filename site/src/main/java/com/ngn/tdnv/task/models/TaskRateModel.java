package com.ngn.tdnv.task.models;

import java.util.ArrayList;
import java.util.List;

import com.ngn.api.tasks.ApiTaskRateModel;
import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class TaskRateModel {
	private String id;
	private long createdTime;
	private int star;
	private String explain;
	private List<String> attachments = new ArrayList<String>();
	private TaskCreatorModel creator;
	
	public TaskRateModel() {
		
	}
	
	public TaskRateModel(ApiTaskRateModel apiTaskRateModel) {
		this.id = apiTaskRateModel.getId();
		this.createdTime = apiTaskRateModel.getCreatedTime();
		this.explain = apiTaskRateModel.getExplain();
		this.attachments.addAll(apiTaskRateModel.getAttachments());
		this.creator = new TaskCreatorModel(apiTaskRateModel.getCreator());
	}
	
	
	public String getCreatedTimeText() {
		return LocalDateUtil.dfDateTime.format(createdTime);
	}
	
}

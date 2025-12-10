package com.ngn.tdnv.task.models;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.tasks.ApiTaskCompletedModel;
import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class TaskCompletedModel {
	private String id;
	private long createdTime;
	private long confirmedTime;
	private long completedTime;
	private String completedStatus;
	private List<Object> attachments;
	private TaskOrgGeneralModel creator;

	public TaskCompletedModel() {

	}

	public TaskCompletedModel(ApiTaskCompletedModel apiCompletedModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiCompletedModel, this);
	}
	
	public String getCompleteTimeText() {
		return completedTime == 0 ? "Đang cập nhật" : LocalDateUtil.dfDateTime.format(completedTime);
	}

}

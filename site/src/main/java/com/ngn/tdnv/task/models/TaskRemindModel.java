package com.ngn.tdnv.task.models;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.tasks.ApiTaskRemindModel;
import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class TaskRemindModel {
	private String id;
	private long createdTime;
	private String reasonRemind;
	private TaskCreatorModel creator;
	private List<String> attachments;
	
	public TaskRemindModel() {
		
	}
	
	public TaskRemindModel(ApiTaskRemindModel apiTaskRemindModel) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.map(apiTaskRemindModel, this);
	}
	
	public String getCreateTimeText() {
		return createdTime == 0 ? "Đang kiểm tra" : LocalDateUtil.dfDateTime.format(createdTime);
	}
	
}

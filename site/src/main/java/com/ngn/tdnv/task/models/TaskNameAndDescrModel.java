package com.ngn.tdnv.task.models;

import org.modelmapper.ModelMapper;

import com.ngn.api.tasks.ApiTaskNameAndDescrModel;

import lombok.Data;

@Data
public class TaskNameAndDescrModel {
	private String name;
	private String description;
	
	public TaskNameAndDescrModel() {
		
	}
	
	public TaskNameAndDescrModel(ApiTaskNameAndDescrModel apiTaskNameAndDescrModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiTaskNameAndDescrModel, this);
	}
	
}

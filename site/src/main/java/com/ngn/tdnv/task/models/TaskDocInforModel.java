package com.ngn.tdnv.task.models;

import org.modelmapper.ModelMapper;

import com.ngn.api.tasks.ApiDocInforOfTaskModel;
import com.ngn.api.tasks.ApiOrgGeneralOfTaskModel;

import lombok.Data;

@Data
public class TaskDocInforModel {
    private String id;
    private long createdTime;
    private String action;
    private String title;
    private String descriptions;
    private ApiOrgGeneralOfTaskModel creator;
    private String number;
    private String symbol;
    private String summary;
    private String category;
	
	public TaskDocInforModel() {
		
	}
	
	public TaskDocInforModel(ApiDocInforOfTaskModel apiDocInforOfTaskModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiDocInforOfTaskModel, this);
	}
	
}

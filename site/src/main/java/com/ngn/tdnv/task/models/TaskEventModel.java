package com.ngn.tdnv.task.models;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.tasks.ApiEventModel;
import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class TaskEventModel {
    private String id;
    private long createdTime;
    private String action;
    private String title;
    private List<TaskNameAndDescrModel> descriptions;
    private TaskOrgGeneralModel creator;
    
    public TaskEventModel() {
    	
    }
    
    public TaskEventModel(ApiEventModel apiEventModel) {
    	ModelMapper mapper = new ModelMapper();
    	descriptions = apiEventModel.getDescriptions().stream().map(TaskNameAndDescrModel::new).toList();
    	mapper.map(apiEventModel, this);
    }
    
    public String getCreateTimeText() {
    	return LocalDateUtil.dfDateTime.format(createdTime);
    }
    
}

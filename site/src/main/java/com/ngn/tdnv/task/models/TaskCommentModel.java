package com.ngn.tdnv.task.models;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.tasks.ApiTaskCommentModel;
import com.ngn.api.tasks.actions.ApiCreatorActionModel;

import lombok.Data;

@Data
public class TaskCommentModel {
	private String id;
	private String idTask;
	private Date createdTime;
	private Object updatedTime;
	private String message;
    private ApiCreatorActionModel creator;
	private List<String> attachments;
	private List<TaskCommentModel> replies;
    private ObjectId objectId = new ObjectId();
    private long createdTimeLong;
    private int updatedTimeLong;
    
    @Data
    public class ObjectId{
    	private int timestamp;
        private Date date;
    }
    
    public TaskCommentModel() {
    	
    }
    
    public TaskCommentModel(ApiTaskCommentModel apiTaskCommentModel) {
    	ModelMapper mapper = new ModelMapper();
    	mapper.map(apiTaskCommentModel, this);
    }
}

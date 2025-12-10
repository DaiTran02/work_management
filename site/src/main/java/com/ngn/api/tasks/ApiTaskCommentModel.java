package com.ngn.api.tasks;

import java.util.Date;
import java.util.List;

import com.ngn.api.tasks.actions.ApiCreatorActionModel;

import lombok.Data;

@Data
public class ApiTaskCommentModel {
	private String id;
	private Date createdTime;
	private Object updatedTime;
	private String message;
    private ApiCreatorActionModel creator;
	private List<String> attachments;
	private List<ApiTaskCommentModel> replies;
    private ObjectId objectId;
    private long createdTimeLong;
    private int updatedTimeLong;
    
    @Data
    public class ObjectId{
    	private int timestamp;
        private Date date;
    }
}

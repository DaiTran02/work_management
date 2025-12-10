package com.ngn.api.tasks;

import org.modelmapper.ModelMapper;

import com.ngn.tdnv.task.models.FollowerModel;

import lombok.Data;

@Data
public class ApiFollowerModel {
    private String organizationId;
    private String organizationName;
    private String organizationGroupId;
    private String organizationGroupName;
    private Object organizationUserId;
    private Object organizationUserName;
    
    public ApiFollowerModel() {
    	
    }
    
    public ApiFollowerModel(FollowerModel followerModel) {
    	ModelMapper mapper = new ModelMapper();
    	mapper.map(followerModel, this);
    }
}

package com.ngn.models.sign_in_org;

import com.ngn.api.sign_in_org.ApiUserExpandModel;

import lombok.Data;

@Data
public class UserExpandModel {
    private String userId;
    private long createdTime;
    private long updatedTime;
    private String userName;
    private String fullName;
    private Object positionName;
    private Object accountIOffice;
    private boolean active;
    private Object status;
    
    public UserExpandModel() {
    	
    }
    
    public UserExpandModel(ApiUserExpandModel apiUserExpandModel) {
    	if(apiUserExpandModel != null) {
        	this.userId = apiUserExpandModel.getUserId();
        	this.createdTime = apiUserExpandModel.getCreatedTime();
        	this.updatedTime = apiUserExpandModel.getUpdatedTime();
        	this.userName = apiUserExpandModel.getUserName();
        	this.fullName = apiUserExpandModel.getFullName();
        	this.positionName = apiUserExpandModel.getPositionName();
        	this.accountIOffice = apiUserExpandModel.getAccountIOffice();
        	this.active = apiUserExpandModel.isActive();
        	this.status = apiUserExpandModel.getStatus();
    	}
    }
    
}

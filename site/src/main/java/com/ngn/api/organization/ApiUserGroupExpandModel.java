package com.ngn.api.organization;

import lombok.Data;

@Data
public class ApiUserGroupExpandModel {
    private String userId;
    private long createdTime;
    private long updatedTime;
    private String userName;
    private String fullName;
    private String positionName;
    private String accountIOffice;
    private boolean active;
    private ApiMoreinfoModel moreInfo;
    private Object status;
    
    public String getPositonText() {
    	return positionName == null ? "Chưa cập nhật" : positionName;
    }
}

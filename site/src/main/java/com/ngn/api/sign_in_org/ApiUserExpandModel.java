package com.ngn.api.sign_in_org;

import lombok.Data;

@Data
public class ApiUserExpandModel {
    private String userId;
    private long createdTime;
    private long updatedTime;
    private String userName;
    private String fullName;
    private Object positionName;
    private Object accountIOffice;
    private boolean active;
    private Object status;
}

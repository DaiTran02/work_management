package com.ngn.api.organization;

import lombok.Data;

@Data
public class ApiUsersRoleModel {
    private String userId;
    private long createdTime;
    private long updatedTime;
    private String userName;
    private String fullName;
    private Object positionName;
    private Object accountIOffice;
    private boolean active;
    private boolean archive;
    private Object status;
}

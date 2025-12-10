package com.ngn.api.notification;

import lombok.Data;

@Data
public class ApiNotifiInfoOrgModel {
    private String organizationId;
    private String organizationName;
    private Object organizationUserId;
    private Object organizationUserName;
}

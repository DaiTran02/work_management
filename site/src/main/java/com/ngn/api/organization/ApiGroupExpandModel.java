package com.ngn.api.organization;

import java.util.List;

import lombok.Data;

@Data
public class ApiGroupExpandModel {
    private String groupId;
    private long createdTime;
    private long updatedTime;
    private String name;
    private String description;
    private String creatorId;
    private String creatorName;
    private List<String> userIds;
}

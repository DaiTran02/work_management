package com.ngn.api.tasks;

import java.util.List;

import lombok.Data;

@Data
public class ApiEventModel {
    private String id;
    private long createdTime;
    private String action;
    private String title;
    private List<ApiTaskNameAndDescrModel> descriptions;
    private ApiOrgGeneralOfTaskModel creator;
}

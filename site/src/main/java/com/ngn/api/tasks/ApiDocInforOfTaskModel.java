package com.ngn.api.tasks;

import lombok.Data;

@Data
public class ApiDocInforOfTaskModel {
    private String id;
    private long createdTime;
    private String action;
    private String title;
    private String descriptions;
    private ApiOrgGeneralOfTaskModel creator;
    private String number;
    private String symbol;
    private String summary;
    private String category;
}

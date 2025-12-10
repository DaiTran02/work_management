package com.ngn.api.tasks;

import java.util.List;

import lombok.Data;

@Data
public class ApiTaskCompletedModel {
	private String id;
    private long createdTime;
    private long confirmedTime;
    private long completedTime;
    private String completedStatus;
    private List<String> attachments;
    private ApiOrgGeneralOfTaskModel creator;
}

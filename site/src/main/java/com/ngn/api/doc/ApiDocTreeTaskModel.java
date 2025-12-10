package com.ngn.api.doc;

import java.util.List;

import lombok.Data;

@Data
public class ApiDocTreeTaskModel {
	private String id;
    private String docId;
    private ApiDocOrgModel fromOrganization;
    private ApiDocOrgModel toOrganization;
    private String title;
    private String description;
    private int countSubTask;
    private List<ApiDocTreeTaskModel> childTasks;
}

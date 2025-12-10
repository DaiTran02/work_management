package com.ngn.api.organization;

import java.util.List;

import lombok.Data;

@Data
public class ApiSubOrganizationRootModel {
	private String id;
    private long createdTime;
    private long updatedTime;
    private String name;
    private String description;
    private String creatorId;
    private String creatorName;
    private String path;
    private Object parentId;
    private List<Object> parentIdSeconds;
    private boolean active;
    private boolean archive;
    private int order;
    private String unitCode;
    private Object organizationCategoryId;
    private int countSubOrganization;
    private List<ApiSubOrganizationRootModel> subOrganizations;
}

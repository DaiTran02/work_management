package com.ngn.api.organization;

import lombok.Data;

@Data
public class ApiCategoriesOrgModel {
    private String id;
    private String name;
    private String description;
    private boolean active;
    private int order;
    private int count;
}

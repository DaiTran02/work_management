package com.ngn.api.tasks.actions;

import java.util.List;

import lombok.Data;

@Data
public class ApiRefuseModel {
    private String reasonRefuse;
    private List<String> attachments;
    private ApiCreatorActionModel creator;
}

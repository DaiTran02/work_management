package com.ngn.tdnv.task.models;

import org.modelmapper.ModelMapper;

import com.ngn.api.organization.ApiCategoriesOrgModel;

import lombok.Data;

@Data
public class CategoriesOrgModel {
    private String id;
    private String name;
    private String description;
    private boolean active;
    private int order;
    private int count;
    
    public CategoriesOrgModel() {
    	
    }
    
    public CategoriesOrgModel(ApiCategoriesOrgModel apiCategoriesOrgModel) {
    	ModelMapper mapper = new ModelMapper();
    	mapper.map(apiCategoriesOrgModel, this);
    }
}

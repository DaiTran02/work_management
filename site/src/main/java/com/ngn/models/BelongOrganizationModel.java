package com.ngn.models;

import com.ngn.api.authentication.ApiAuthenBelongOrganizationsModel;

import lombok.Data;

@Data
public class BelongOrganizationModel {
	private String organizationId;
    private String organizationName;
    
    public BelongOrganizationModel() {
    	
    }
    
    public BelongOrganizationModel(ApiAuthenBelongOrganizationsModel apiBelongOrganizationsModel) {
    	this.organizationId = apiBelongOrganizationsModel.getOrganizationId();
    	this.organizationName = apiBelongOrganizationsModel.getOrganizationName();
    }
}

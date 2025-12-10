package com.ngn.api.authentication;

import lombok.Data;

@Data
public class ApiAuthenBelongOrganizationsModel {
	private String organizationId;
    private String organizationName;
    
    public ApiAuthenBelongOrganizationsModel() {
    	
    }
    
	public ApiAuthenBelongOrganizationsModel(String organizationId, String organizationName) {
		this.organizationId = organizationId;
		this.organizationName = organizationName;
	}
    
    
}

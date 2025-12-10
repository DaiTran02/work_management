package vn.com.ngn.page.organization.models;

import lombok.Data;
import vn.com.ngn.api.organization.ApiOrganizationModel;

@Data
public class CreateAndUpdateOrgModel {
	private String name;
    private String description;
    private String parentId;
    private boolean active;
    private String unitCode;
    private int order;
    private String organizationCategoryId;
    private String organizationLevel;
    
    public CreateAndUpdateOrgModel() {
    	
    }
    
    public CreateAndUpdateOrgModel(ApiOrganizationModel apiOrganizationModel) {
    	this.name = apiOrganizationModel.getName();
    	this.description = apiOrganizationModel.getDescription();
    	this.parentId = (String) apiOrganizationModel.getParentId();
    	this.active = apiOrganizationModel.isActive();
    	this.unitCode = apiOrganizationModel.getUnitCode();
    	this.order = apiOrganizationModel.getOrder();
    	this.organizationCategoryId = apiOrganizationModel.getOrganizationCategoryId();
    	this.organizationLevel = apiOrganizationModel.getLevel().getKey();
    }
}

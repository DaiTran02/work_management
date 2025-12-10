package vn.com.ngn.api.organization;

import lombok.Data;
import vn.com.ngn.page.organization.models.CreateAndUpdateOrgModel;

@Data
public class ApiCreateAndUpdateOrgModel {
    private String name;
    private String description;
    private String parentId;
    private boolean active;
    private String unitCode;
    private int order;
    private String organizationCategoryId;
    private String level;
    
    public ApiCreateAndUpdateOrgModel(){
    	
    }
    
    public ApiCreateAndUpdateOrgModel(CreateAndUpdateOrgModel createAndUpdateOrgModel) {
    	this.name = createAndUpdateOrgModel.getName();
    	this.description = createAndUpdateOrgModel.getDescription();
    	this.parentId = createAndUpdateOrgModel.getParentId();
    	this.active = createAndUpdateOrgModel.isActive();
    	this.unitCode = createAndUpdateOrgModel.getUnitCode();
    	this.order = createAndUpdateOrgModel.getOrder();
    	this.organizationCategoryId = createAndUpdateOrgModel.getOrganizationCategoryId();
    	this.level = createAndUpdateOrgModel.getOrganizationLevel();
    }
}

package vn.com.ngn.page.organization.models;

import lombok.Data;
import vn.com.ngn.api.organization.ApiPermissionModel;

@Data
public class PermissionModel {
    private String id;
    private String key;
    private String name;
    private String description;
    private int orderSort;
    private String groupId;
    private String groupName;
    private int groupSort;
    
    public PermissionModel() {
    	
    }
    
    public PermissionModel(ApiPermissionModel apiPermissionModel) {
    	this.id = apiPermissionModel.getId();
    	this.key = apiPermissionModel.getKey();
    	this.name = apiPermissionModel.getName();
    	this.description = apiPermissionModel.getDescription();
    	this.orderSort = apiPermissionModel.getOrderSort();
    	this.groupId = apiPermissionModel.getGroupId();
    	this.groupName = apiPermissionModel.getGroupName();
    	this.groupSort = apiPermissionModel.getGroupSort();
    	
    }
}

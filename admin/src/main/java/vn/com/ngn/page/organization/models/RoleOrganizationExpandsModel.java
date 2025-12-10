package vn.com.ngn.page.organization.models;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;

@Data
public class RoleOrganizationExpandsModel {
	private String id;
	private String roleId;
    private Date createdTime;
    private Date updatedTime;
    private String name;
    private String description;
    private String creatorId;
    private String creatorName;
    private List<String> permissionKeys;
    private List<String> userIds;
    private String roleTemplateId;
    private long createdTimeLong;
    private long updatedTimeLong;
    
    public RoleOrganizationExpandsModel() {
    	
    }
    
    public RoleOrganizationExpandsModel(ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel) {
    	this.id = apiRoleOrganizationExpandsModel.getId();
    	this.roleId = apiRoleOrganizationExpandsModel.getRoleId();
    	this.createdTime = apiRoleOrganizationExpandsModel.getCreatedTime();
    	this.updatedTime = apiRoleOrganizationExpandsModel.getUpdatedTime();
    	this.name = apiRoleOrganizationExpandsModel.getName();
    	this.description = apiRoleOrganizationExpandsModel.getDescription();
    	this.creatorId = apiRoleOrganizationExpandsModel.getCreatorId();
    	this.creatorName = apiRoleOrganizationExpandsModel.getCreatorName();
    	this.permissionKeys = apiRoleOrganizationExpandsModel.getPermissionKeys();
    	this.userIds = apiRoleOrganizationExpandsModel.getUserIds();
    	this.roleTemplateId = apiRoleOrganizationExpandsModel.getRoleTemplateId();
    	this.createdTimeLong = apiRoleOrganizationExpandsModel.getCreatedTimeLong();
    	this.updatedTimeLong = apiRoleOrganizationExpandsModel.getUpdatedTimeLong();
    }
}

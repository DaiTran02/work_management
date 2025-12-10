package vn.com.ngn.api.organization;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;

@Data
public class ApiRoleOrganizationExpandsModel {
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
    
    public ApiRoleOrganizationExpandsModel() {
    	
    }
    
    public ApiRoleOrganizationExpandsModel(RoleOrganizationExpandsModel roleOrganizationExpandsModel){
    	this.roleId = roleOrganizationExpandsModel.getRoleId();
    	this.createdTime = roleOrganizationExpandsModel.getCreatedTime();
    	this.updatedTime = roleOrganizationExpandsModel.getUpdatedTime();
    	this.name = roleOrganizationExpandsModel.getName();
    	this.description = roleOrganizationExpandsModel.getDescription();
    	this.creatorId = roleOrganizationExpandsModel.getCreatorId();
    	this.creatorName = roleOrganizationExpandsModel.getCreatorName();
    	this.permissionKeys = roleOrganizationExpandsModel.getPermissionKeys();
    	this.userIds = roleOrganizationExpandsModel.getUserIds();
    	this.roleTemplateId = roleOrganizationExpandsModel.getRoleTemplateId();
    	this.createdTimeLong = roleOrganizationExpandsModel.getCreatedTimeLong();
    	this.updatedTimeLong = roleOrganizationExpandsModel.getUpdatedTimeLong();
    	
    }
}

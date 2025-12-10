package vn.com.ngn.api.organization;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import vn.com.ngn.page.organization.models.OrganizationModel;

@Data
public class ApiOrganizationModel {
    private String id;
    private long createdTime;
    private long updatedTime;
    private String name;
    private String description;
    private String creatorId;
    private String creatorName;
    private String path;
    private Object parentId;
    private List<Object> parentIdSeconds;
    private boolean active;
    private int order;
    private String unitCode;
    private String organizationCategoryId;
    private int countSubOrganization;
    private ApiKeyAndValueOrgModel level;
    private List<ApiUserOrganizationExpandsModel> userOrganizationExpands;
    private List<ApiGroupOrganizationExpandsModel> groupOrganizationExpands;
    private List<ApiRoleOrganizationExpandsModel> roleOrganizationExpands;
    
    public ApiOrganizationModel() {
    	
    }
    
    public ApiOrganizationModel(OrganizationModel organizationModel) {
    	this.id = organizationModel.getId();
    	this.createdTime = organizationModel.getCreatedTime();
    	this.updatedTime = organizationModel.getUpdatedTime();
    	this.name = organizationModel.getName();
    	this.description = organizationModel.getDescription();
    	this.creatorId = organizationModel.getCreatorId();
    	this.creatorName = organizationModel.getCreatorName();
    	this.path = organizationModel.getPath();
    	this.parentId = organizationModel.getParentId();
    	this.parentIdSeconds = organizationModel.getParentIdSeconds();
    	this.active = organizationModel.isActive();
    	this.order = organizationModel.getOrder();
    	this.unitCode = organizationModel.getUnitCode();
    	this.level = organizationModel.getLevel();
    	this.countSubOrganization = organizationModel.getCountSubOrganization();
    	this.userOrganizationExpands = organizationModel.getUserOrganizationExpands().stream().map(ApiUserOrganizationExpandsModel::new).collect(Collectors.toList());
    	this.groupOrganizationExpands = organizationModel.getGroupOrganizationExpands().stream().map(ApiGroupOrganizationExpandsModel::new).collect(Collectors.toList());
    	this.roleOrganizationExpands = organizationModel.getRoleOrganizationExpands().stream().map(ApiRoleOrganizationExpandsModel::new).collect(Collectors.toList());
    	this.organizationCategoryId = organizationModel.getOrganizationCategoryId();
    }
    
}

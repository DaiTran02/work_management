package vn.com.ngn.page.report.models;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import vn.com.ngn.api.report.ApiOrganizationForReportModel;

@Data
public class OrganizationForReportModel {
	private String id;
    private long createdTime;
    private long updatedTime;
    private String name;
    private String description;
    private String creatorId;
    private String creatorName;
    private String path;
    private Object parentId;
    private List<String> parentIdSeconds;
    private boolean active;
    private int order;
    private String unitCode;
    private int countSubOrganization;
    private List<OrganizationForReportModel> subOrganizations;
    private List<UserOrganizationModel> userOrganizationExpands;
    
    public OrganizationForReportModel() {
    	
    }
    
    public OrganizationForReportModel(ApiOrganizationForReportModel apiOrganizationForReportModel) {
    	this.id = apiOrganizationForReportModel.getId();
    	this.createdTime = apiOrganizationForReportModel.getCreatedTime();
    	this.updatedTime = apiOrganizationForReportModel.getUpdatedTime();
    	this.name = apiOrganizationForReportModel.getName();
    	this.description = apiOrganizationForReportModel.getDescription();
    	this.creatorId = apiOrganizationForReportModel.getCreatorId();
    	this.creatorName = apiOrganizationForReportModel.getCreatorName();
    	this.path = apiOrganizationForReportModel.getPath();
    	this.parentId = apiOrganizationForReportModel.getParentId();
    	this.parentIdSeconds = apiOrganizationForReportModel.getParentIdSeconds();
    	this.active = apiOrganizationForReportModel.isActive();
    	this.order = apiOrganizationForReportModel.getOrder(); 
    	this.unitCode = apiOrganizationForReportModel.getUnitCode();
    	this.countSubOrganization = apiOrganizationForReportModel.getCountSubOrganization();
    	this.subOrganizations = apiOrganizationForReportModel.getSubOrganizations() == (null) ? null : apiOrganizationForReportModel.getSubOrganizations().stream().map(OrganizationForReportModel::new).collect(Collectors.toList());
    	this.userOrganizationExpands = apiOrganizationForReportModel.getUserOrganizationExpands() == null ? Collections.emptyList() : apiOrganizationForReportModel.getUserOrganizationExpands().stream().map(UserOrganizationModel::new).collect(Collectors.toList());
    }
}

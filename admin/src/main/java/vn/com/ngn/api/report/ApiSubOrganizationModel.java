package vn.com.ngn.api.report;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import vn.com.ngn.page.report.models.SubOrganizationModel;

@Data
public  class ApiSubOrganizationModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String name;
	private String description;
	private String creatorId;
	private String creatorName;
	private String path;
	private String parentId;
	private List<String> parentIdSeconds;
	private boolean active;
	private int order;
	private String unitCode;
	private int countSubOrganization;
	private List<ApiSubOrganizationModel> subOrganizations;
	private List<ApiUserOrganizationModel> userOrganizationExpands;
	
	public ApiSubOrganizationModel() {
		
	}
	
	public ApiSubOrganizationModel(SubOrganizationModel subOrganizationModel) {
		this.id = subOrganizationModel.getId();
		this.createdTime = subOrganizationModel.getCreatedTime();
		this.updatedTime = subOrganizationModel.getUpdatedTime();
		this.name = subOrganizationModel.getName();
		this.description = subOrganizationModel.getDescription();
		this.creatorId = subOrganizationModel.getCreatorId();
		this.creatorName = subOrganizationModel.getCreatorName();
		this.path = subOrganizationModel.getPath();
		this.parentId = subOrganizationModel.getParentId();
		this.parentIdSeconds = subOrganizationModel.getParentIdSeconds();
		this.active = subOrganizationModel.isActive();
		this.order = subOrganizationModel.getOrder();
		this.unitCode = subOrganizationModel.getUnitCode();
		this.countSubOrganization = subOrganizationModel.getCountSubOrganization();
		this.subOrganizations = subOrganizationModel.getSubOrganizations().stream().map(ApiSubOrganizationModel::new).collect(Collectors.toList());
		this.userOrganizationExpands = subOrganizationModel.getUserOrganizationExpands().stream().map(ApiUserOrganizationModel::new).collect(Collectors.toList());
	}
	
	
	
	
	
	
}

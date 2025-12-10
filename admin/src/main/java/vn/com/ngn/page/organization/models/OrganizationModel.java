package vn.com.ngn.page.organization.models;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import vn.com.ngn.api.organization.ApiKeyAndValueOrgModel;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.utils.LocalDateUtils;

@Data
public class OrganizationModel {
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
	 private int countSubOrganization;
	 private ApiKeyAndValueOrgModel level;
	private List<UserOrganizationExpandsModel> userOrganizationExpands;
	private List<GroupOrganizationExpandsModel> groupOrganizationExpands;
	private List<RoleOrganizationExpandsModel> roleOrganizationExpands;
	private OrganizationModel organizationModel;
	private String organizationCategoryId;
	
	public OrganizationModel() {
		
	}
	
	public OrganizationModel(ApiOrganizationModel apiOrganizationModel) {
		this.id = apiOrganizationModel.getId();
		this.createdTime = apiOrganizationModel.getCreatedTime();
		this.updatedTime = apiOrganizationModel.getUpdatedTime();
		this.name = apiOrganizationModel.getName();
		this.description = apiOrganizationModel.getDescription();
		this.creatorId = apiOrganizationModel.getCreatorId();
		this.creatorName = apiOrganizationModel.getCreatorName();
		this.path = apiOrganizationModel.getPath();
		this.parentId = apiOrganizationModel.getParentId();
		this.parentIdSeconds = apiOrganizationModel.getParentIdSeconds();
		this.active = apiOrganizationModel.isActive();
		this.order = apiOrganizationModel.getOrder();
		this.unitCode = apiOrganizationModel.getUnitCode();
		this.countSubOrganization = apiOrganizationModel.getCountSubOrganization();
		this.level = apiOrganizationModel.getLevel();
		this.userOrganizationExpands = apiOrganizationModel.getUserOrganizationExpands().stream().map(UserOrganizationExpandsModel::new).collect(Collectors.toList());
		this.groupOrganizationExpands = apiOrganizationModel.getGroupOrganizationExpands().stream().map(GroupOrganizationExpandsModel::new).collect(Collectors.toList());
		this.roleOrganizationExpands = apiOrganizationModel.getRoleOrganizationExpands().stream().map(RoleOrganizationExpandsModel::new).collect(Collectors.toList());
		this.organizationCategoryId = apiOrganizationModel.getOrganizationCategoryId();
	}
	
	public String getUpdateTimeText() {
		return updatedTime == 0 ? "Chưa cập nhật" : LocalDateUtils.dfDate.format(updatedTime);
	}
	
}
















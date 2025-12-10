package vn.com.ngn.api.organization;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.com.ngn.page.organization.models.GroupOrganizationExpandsModel;

@Data
public class ApiGroupOrganizationExpandsModel {
	private String groupId;
	private Date createdTime;
	private Date updatedTime;
	private String name;
	private String description;
	private String creatorId;
	private String creatorName;
	private List<String> userIds;
	private long createdTimeLong;
	private long updatedTimeLong;
	private int order;
	
	public ApiGroupOrganizationExpandsModel() {
		
	}
	
	public ApiGroupOrganizationExpandsModel(GroupOrganizationExpandsModel groupOrganizationExpandsModel) {
		this.groupId = groupOrganizationExpandsModel.getGroupId();
		this.createdTime = groupOrganizationExpandsModel.getCreatedTime();
		this.updatedTime = groupOrganizationExpandsModel.getUpdatedTime();
		this.name = groupOrganizationExpandsModel.getName();
		this.description = groupOrganizationExpandsModel.getDescription();
		this.creatorId = groupOrganizationExpandsModel.getCreatorId();
		this.creatorName = groupOrganizationExpandsModel.getCreatorName();
		this.userIds = groupOrganizationExpandsModel.getUserIds();
		this.createdTimeLong = groupOrganizationExpandsModel.getCreatedTimeLong();
		this.updatedTimeLong = groupOrganizationExpandsModel.getUpdatedTimeLong();
		this.order = groupOrganizationExpandsModel.getOrder();
	}

}

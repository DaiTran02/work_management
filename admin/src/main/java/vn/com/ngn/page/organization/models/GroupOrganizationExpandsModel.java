package vn.com.ngn.page.organization.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.com.ngn.api.organization.ApiGroupOrganizationExpandsModel;

@Data
public class GroupOrganizationExpandsModel {
	private String groupId;
	private Date createdTime;
	private Date updatedTime;
	private String name;
	private String description;
	private String creatorId;
	private String creatorName;
	private List<String> userIds = new ArrayList<String>();
	private long createdTimeLong;
	private long updatedTimeLong;
	private int order;
	
	public GroupOrganizationExpandsModel() {
		
	}
	
	public GroupOrganizationExpandsModel(ApiGroupOrganizationExpandsModel apiGroupOrganizationExpandsModel) {
		this.groupId = apiGroupOrganizationExpandsModel.getGroupId();
		this.createdTime = apiGroupOrganizationExpandsModel.getCreatedTime();
		this.updatedTime = apiGroupOrganizationExpandsModel.getUpdatedTime();
		this.name = apiGroupOrganizationExpandsModel.getName();
		this.description = apiGroupOrganizationExpandsModel.getDescription();
		this.creatorId = apiGroupOrganizationExpandsModel.getCreatorId();
		this.creatorName = apiGroupOrganizationExpandsModel.getCreatorName();
		this.userIds = apiGroupOrganizationExpandsModel.getUserIds();
		this.createdTimeLong = apiGroupOrganizationExpandsModel.getCreatedTimeLong();
		this.updatedTimeLong = apiGroupOrganizationExpandsModel.getUpdatedTimeLong();
		this.order = apiGroupOrganizationExpandsModel.getOrder();
	}
}

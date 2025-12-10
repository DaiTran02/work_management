package vn.com.ngn.page.app_access.models;

import java.util.List;

import lombok.Data;
import vn.com.ngn.api.app_access.ApiAppAccessModel;

@Data
public class AppAccessModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String apiKey;
	private long startTime;
	private long endTime;
	private String name;
	private String description;
	private String creatorId;
	private String creatorName;
	private String organizationId;
	private List<String> ipsAccess;
	private boolean active;
	
	public AppAccessModel() {
		
	}
	
	public AppAccessModel(ApiAppAccessModel apiAppAccessModel) {
		this.id = apiAppAccessModel.getId();
		this.createdTime = apiAppAccessModel.getCreatedTime();
		this.updatedTime = apiAppAccessModel.getUpdatedTime();
		this.apiKey = apiAppAccessModel.getApiKey();
		this.startTime = apiAppAccessModel.getStartTime();
		this.endTime = apiAppAccessModel.getEndTime();
		this.name = apiAppAccessModel.getName();
		this.description = apiAppAccessModel.getDescription();
		this.creatorName = apiAppAccessModel.getCreatorName();
		this.organizationId = apiAppAccessModel.getOrganizationId();
		this.ipsAccess = apiAppAccessModel.getIpsAccess();
		this.active = apiAppAccessModel.isActive();
	}
}

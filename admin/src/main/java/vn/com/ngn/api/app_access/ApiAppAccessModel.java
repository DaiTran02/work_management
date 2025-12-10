package vn.com.ngn.api.app_access;

import java.util.List;

import lombok.Data;
import vn.com.ngn.page.app_access.models.AppAccessModel;

@Data
public class ApiAppAccessModel {
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
	
	public ApiAppAccessModel() {
		
	}
	
	public ApiAppAccessModel(AppAccessModel appAccessModel) {
		this.id = appAccessModel.getId();
		this.createdTime = appAccessModel.getCreatedTime();
		this.updatedTime = appAccessModel.getUpdatedTime();
		this.apiKey = appAccessModel.getApiKey();
		this.startTime = appAccessModel.getStartTime();
		this.endTime = appAccessModel.getEndTime();
		this.name = appAccessModel.getName();
		this.description = appAccessModel.getDescription();
		this.creatorName = appAccessModel.getCreatorName();
		this.organizationId = appAccessModel.getOrganizationId();
		this.ipsAccess = appAccessModel.getIpsAccess();
		this.active = appAccessModel.isActive();
	}
}

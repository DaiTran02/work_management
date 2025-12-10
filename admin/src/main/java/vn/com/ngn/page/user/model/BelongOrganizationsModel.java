package vn.com.ngn.page.user.model;

import lombok.Data;
import vn.com.ngn.api.auth.ApiBelongOrganizationsModel;
import vn.com.ngn.api.organization.ApiOrganizationModel;

@Data
public class BelongOrganizationsModel {
	private String organizationId;
	private String organizationName;
	
	public BelongOrganizationsModel() {
		
	}
	
	public BelongOrganizationsModel(ApiBelongOrganizationsModel apiBelongOrganizationsModel) {
		this.organizationId = apiBelongOrganizationsModel.getOrganizationId();
		this.organizationName = apiBelongOrganizationsModel.getOrganizationName();
	}
	
	public BelongOrganizationsModel(ApiOrganizationModel apiOrganizationModel) {
		this.organizationId = apiOrganizationModel.getId();
		this.organizationName = apiOrganizationModel.getName();
	}
	
}

package vn.com.ngn.page.organization.models;

import lombok.Data;
import vn.com.ngn.api.organization.ApiUserOrganizationExpandsModel;
import vn.com.ngn.page.report.models.MoreInfoModel;

@Data 
public class UserOrganizationExpandsModel {
	private String userId;
	private long createdTime;
	private long updatedTime;
	private String userName;
	private String positionName;
	private String accountIOffice;
	private boolean active;
	private MoreInfoModel moreInfo;
	private String status;
	
	public UserOrganizationExpandsModel(ApiUserOrganizationExpandsModel apiUserOrganizationExpandsModel) {
		this.userId = apiUserOrganizationExpandsModel.getUserId();
		this.createdTime = apiUserOrganizationExpandsModel.getCreatedTime();
		this.updatedTime = apiUserOrganizationExpandsModel.getUpdatedTime();
		this.userName = apiUserOrganizationExpandsModel.getUserName();
		this.positionName = apiUserOrganizationExpandsModel.getPositionName();
		this.accountIOffice = apiUserOrganizationExpandsModel.getAccountIOffice();
		this.active = apiUserOrganizationExpandsModel.isActive();
		if(apiUserOrganizationExpandsModel.getMoreInfo() != null) {
			this.moreInfo = new MoreInfoModel(apiUserOrganizationExpandsModel.getMoreInfo());
		}
		this.status = apiUserOrganizationExpandsModel.getStatus();
	}
}

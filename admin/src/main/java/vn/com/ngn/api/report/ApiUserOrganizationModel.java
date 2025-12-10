package vn.com.ngn.api.report;

import lombok.Data;
import vn.com.ngn.page.report.models.UserOrganizationModel;

@Data
public class ApiUserOrganizationModel {
	private String userId;
	private long createdTime;
	private long updatedTime;
	private String userName;
	private String positionName;
	private String accountIOffice;
	private boolean active;
	private ApiMoreInfoModel moreInfo;
	private String status;

	public ApiUserOrganizationModel() {
		
	}
	
	public ApiUserOrganizationModel(UserOrganizationModel userOrganizationModel) {
		this.userId = userOrganizationModel.getUserId();
		this.createdTime = userOrganizationModel.getCreatedTime();
		this.updatedTime = userOrganizationModel.getUpdatedTime();
		this.userName = userOrganizationModel.getUserName();
		this.positionName = userOrganizationModel.getPositionName();
		this.accountIOffice = userOrganizationModel.getAccountIOffice();
		this.active = userOrganizationModel.isActive();
	}
}

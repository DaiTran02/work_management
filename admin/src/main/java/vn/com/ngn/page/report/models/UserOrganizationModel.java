package vn.com.ngn.page.report.models;

import lombok.Data;
import vn.com.ngn.api.report.ApiUserOrganizationModel;

@Data
public class UserOrganizationModel {
	private String userId;
	private long createdTime;
	private long updatedTime;
	private String userName;
	private String positionName;
	private String accountIOffice;
	private boolean active;
	private MoreInfoModel moreInfo;
	private String status;
	private String orgName;
	
	public UserOrganizationModel() {
		
	}
	
	public UserOrganizationModel(ApiUserOrganizationModel userOrganizationModel) {
		this.userId = userOrganizationModel.getUserId();
		this.createdTime = userOrganizationModel.getCreatedTime();
		this.updatedTime = userOrganizationModel.getUpdatedTime();
		this.userName = userOrganizationModel.getUserName();
		this.positionName = userOrganizationModel.getPositionName();
		this.accountIOffice = userOrganizationModel.getAccountIOffice();
		this.active = userOrganizationModel.isActive();
		this.moreInfo = new MoreInfoModel(userOrganizationModel.getMoreInfo());
		this.status = userOrganizationModel.getStatus();
	}
	
	public String getAccountIOfficeText() {
		if(accountIOffice == null)
			return "Không có tài khoản";
		
		return accountIOffice.isBlank() ? "Không có tài khoản" : accountIOffice;
	}
	
	
	public String getPositionText() {
		if(positionName == null) 
			return "Chưa cập nhật";
		
		return positionName.isBlank() ? "Chưa cập nhật" : positionName;
	}
	
	public String getStatusText() {
		return status.equals("logged") ? "Sử dụng" : "Không sử dụng";
	}
	
	public String getClassStatus() {
		return status.equals("logged") ? "act" : "not_act";
	}
	
}

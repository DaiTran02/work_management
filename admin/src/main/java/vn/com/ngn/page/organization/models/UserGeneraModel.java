package vn.com.ngn.page.organization.models;

import lombok.Data;
import vn.com.ngn.api.organization.ApiUserGeneraModel;

@Data
public class UserGeneraModel {
	private String userId;
	private long createdTime;
	private long updatedTime;
	private String userName;
	private String fullName;
	private Object positionName;
	private Object accountIOffice;
	private boolean active;
	
	public UserGeneraModel() {
		
	}
	
	public UserGeneraModel(ApiUserGeneraModel apiUserOfGroupModel) {
		this.userId = apiUserOfGroupModel.getUserId();
		this.createdTime = apiUserOfGroupModel.getCreatedTime();
		this.updatedTime = apiUserOfGroupModel.getUpdatedTime();
		this.userName = apiUserOfGroupModel.getUserName();
		this.positionName = apiUserOfGroupModel.getPositionName() == null ? "Đang xử lý" : apiUserOfGroupModel.getPositionName();
		this.accountIOffice = apiUserOfGroupModel.getAccountIOffice();
		this.active = apiUserOfGroupModel.isActive();
		this.fullName = apiUserOfGroupModel.getFullName();
	}
	
	
}

package vn.com.ngn.api.organization;

import lombok.Data;
import vn.com.ngn.page.organization.models.UserGeneraModel;

@Data
public class ApiUserGeneraModel {
	private String userId;
	private long createdTime;
	private long updatedTime;
	private String userName;
	private String fullName;
	private Object positionName;
	private Object accountIOffice;
	private boolean active;
	
	public ApiUserGeneraModel() {
		
	}
	
	public ApiUserGeneraModel(UserGeneraModel userGeneraModel) {
		this.userId = userGeneraModel.getUserId();
		this.createdTime = userGeneraModel.getCreatedTime();
		this.updatedTime = userGeneraModel.getUpdatedTime();
		this.userName = userGeneraModel.getUserName();
		this.positionName = userGeneraModel.getPositionName();
		this.accountIOffice = userGeneraModel.getAccountIOffice();
		this.active = userGeneraModel.isActive();
		this.fullName = userGeneraModel.getFullName();
	}

}

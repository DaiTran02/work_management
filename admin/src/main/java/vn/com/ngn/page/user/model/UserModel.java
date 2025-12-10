package vn.com.ngn.page.user.model;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import vn.com.ngn.api.organization.ApiKeyAndValueOrgModel;
import vn.com.ngn.api.user.ApiFirstReview;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.utils.LocalDateUtils;

@Data
public class UserModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String username;
	private String password;
	private String email;
	private Object phone;
	private String fullName;
	private String jobTitle;
	private Object creatorId;
	private Object creatorName;
	private boolean active;
	private String activeCode;
	private long lastDateLogin;
	private long lastChangePassword;
	private Object lastIPLogin;
	private List<String> organizationIds;
	private List<BelongOrganizationsModel> belongOrganizations;
	private ApiFirstReview firstReview;
	private ApiKeyAndValueOrgModel provider;
	public UserModel() {
		
	}
	
	public UserModel(ApiUserModel apiUserModel) {
		this.id = apiUserModel.getId();
		this.createdTime = apiUserModel.getCreatedTime();
		this.updatedTime = apiUserModel.getUpdatedTime();
		this.username = apiUserModel.getUsername();
		this.email = apiUserModel.getEmail();
		this.phone = apiUserModel.getPhone();
		this.fullName = apiUserModel.getFullName();
		this.jobTitle = apiUserModel.getJobTitle();
		this.creatorId = apiUserModel.getCreatorId();
		this.creatorName = apiUserModel.getCreatorName();
		this.active = apiUserModel.isActive();
		this.activeCode = apiUserModel.getActiveCode();
		this.lastDateLogin = apiUserModel.getLastDateLogin();
		this.lastChangePassword = apiUserModel.getLastChangePassword();
		this.lastIPLogin = apiUserModel.getLastIPLogin();
		this.organizationIds = apiUserModel.getOrganizationIds();
		this.belongOrganizations = apiUserModel.getBelongOrganizations().stream().map(BelongOrganizationsModel::new).collect(Collectors.toList());
		if(apiUserModel.getFirstReview() != null) {
			this.firstReview = new ApiFirstReview(apiUserModel.getFirstReview());
		}
		this.provider = apiUserModel.getProvider();
	}
	
	public String getCreatorName() {
		return creatorName == null ? "" : creatorName.toString();
	}
	
	public String getEmailText() {
		return email.isEmpty() ? "Chưa cập nhật" : email;
	}
	
	public String getPhoneText() {
		return phone == null ? "Chưa cập nhật" : phone.toString();
	}
	
	public String getActiveCodeText() {
		return activeCode == null ? "Chưa cập nhật" : activeCode;
	}
	
	public String getLastDateLoginText() {
		return lastDateLogin == 0 ? "Chưa đăng nhập" : LocalDateUtils.dfDate.format(lastDateLogin);
	}
	
	public String getCreateTimeText() {
		return LocalDateUtils.dfDate.format(createdTime);
	}
	
	public String getLastChangePassText() {
		return lastChangePassword == 0 ? "Chưa đổi mật khẩu" : LocalDateUtils.dfDate.format(lastChangePassword);
	}
	
	public String getActiveText() {
		return active == true ? "Hoạt động" : "Không hoạt động";
	}

}

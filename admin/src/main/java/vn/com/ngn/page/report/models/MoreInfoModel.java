package vn.com.ngn.page.report.models;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import vn.com.ngn.api.report.ApiMoreInfoModel;
import vn.com.ngn.page.user.model.BelongOrganizationsModel;

@Data
public class MoreInfoModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String username;
	private String email;
	private String phone;
	private String fullName;
	private Object jobTitle;
	private String creatorId;
	private String creatorName;
	private boolean active;
	private String activeCode;
	private int lastDateLogin;
	private long lastChangePassword;
	private Object lastIPLogin;
	private List<BelongOrganizationsModel> belongOrganizations;
	
	public MoreInfoModel() {
		
	}
	
	public MoreInfoModel(ApiMoreInfoModel apiMoreInfoModel) {
		this.id = apiMoreInfoModel.getId();
		this.createdTime = apiMoreInfoModel.getCreatedTime();
		this.updatedTime = apiMoreInfoModel.getUpdatedTime();
		this.username = apiMoreInfoModel.getUsername();
		this.email = apiMoreInfoModel.getEmail();
		this.phone = apiMoreInfoModel.getPhone();
		this.fullName = apiMoreInfoModel.getFullName();
		this.jobTitle = apiMoreInfoModel.getJobTitle();
		this.creatorId = apiMoreInfoModel.getCreatorId();
		this.creatorName = apiMoreInfoModel.getCreatorName();
		this.active = apiMoreInfoModel.isActive();
		this.activeCode = apiMoreInfoModel.getActiveCode();
		this.lastDateLogin = apiMoreInfoModel.getLastDateLogin();
		this.lastChangePassword = apiMoreInfoModel.getLastChangePassword();
		this.lastIPLogin = apiMoreInfoModel.getLastIPLogin();
		this.belongOrganizations = apiMoreInfoModel.getBelongOrganizations().stream().map(BelongOrganizationsModel::new).collect(Collectors.toList());
	}
	
}

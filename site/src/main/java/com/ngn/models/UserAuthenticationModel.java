package com.ngn.models;

import java.util.List;
import java.util.stream.Collectors;

import com.ngn.api.authentication.ApiAuthenticationModel;

import lombok.Data;

@Data
public class UserAuthenticationModel {
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
	private List<BelongOrganizationModel> belongOrganizations;
	private List<BelongOrganizationModel> belongParentOrganizations;
	private String loginToken;
	
	public UserAuthenticationModel() {
		
	}
	
	public UserAuthenticationModel(ApiAuthenticationModel apiAuthenticationModel) {
		this.id = apiAuthenticationModel.getId();
		this.createdTime = apiAuthenticationModel.getCreatedTime();
		this.updatedTime = apiAuthenticationModel.getUpdatedTime();
		this.username = apiAuthenticationModel.getUsername();
		this.email = apiAuthenticationModel.getEmail();
		this.phone = apiAuthenticationModel.getPhone();
		this.fullName = apiAuthenticationModel.getFullName();
		this.jobTitle = apiAuthenticationModel.getJobTitle();
		this.creatorId = apiAuthenticationModel.getCreatorId();
		this.creatorName = apiAuthenticationModel.getCreatorName();
		this.active = apiAuthenticationModel.isActive();
		this.activeCode = apiAuthenticationModel.getActiveCode();
		this.lastChangePassword = apiAuthenticationModel.getLastChangePassword();
		this.lastIPLogin = apiAuthenticationModel.getLastIPLogin();
		this.belongOrganizations = apiAuthenticationModel.getBelongOrganizations().stream().map(BelongOrganizationModel::new).collect(Collectors.toList());
		this.belongParentOrganizations = apiAuthenticationModel.getBelongParentOrganizations().stream().map(BelongOrganizationModel::new).collect(Collectors.toList());
		this.loginToken = apiAuthenticationModel.getLoginToken();
	}
}

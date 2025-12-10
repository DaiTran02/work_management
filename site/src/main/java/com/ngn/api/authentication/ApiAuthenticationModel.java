package com.ngn.api.authentication;

import java.util.List;

import com.ngn.api.utils.ApiKeyValueModel;

import lombok.Data;

@Data
public  class ApiAuthenticationModel {
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
	private List<ApiAuthenBelongOrganizationsModel> belongOrganizations;
	private List<ApiAuthenBelongOrganizationsModel> belongParentOrganizations;
	private String loginToken;
	private long expiryToken;
	private String refeshToken;
	private boolean guideWebUI;
	private ApiKeyValueModel provider;
}

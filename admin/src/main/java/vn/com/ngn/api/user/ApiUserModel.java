package vn.com.ngn.api.user;

import java.util.List;

import lombok.Data;
import vn.com.ngn.api.auth.ApiBelongOrganizationsModel;
import vn.com.ngn.api.organization.ApiKeyAndValueOrgModel;

@Data
public class ApiUserModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String username;
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
	private List<ApiBelongOrganizationsModel> belongOrganizations;
	private ApiFirstReview firstReview;
	private ApiKeyAndValueOrgModel provider;
}

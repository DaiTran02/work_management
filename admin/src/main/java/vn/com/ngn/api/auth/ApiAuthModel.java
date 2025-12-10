package vn.com.ngn.api.auth;

import java.util.List;

import lombok.Data;

@Data
public class ApiAuthModel {
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
	private Object activeCode;
	private long lastDateLogin;
	private long lastChangePassword;
	private Object lastIPLogin;
	private List<ApiBelongOrganizationsModel> belongOrganizations;
	private String loginToken;
}

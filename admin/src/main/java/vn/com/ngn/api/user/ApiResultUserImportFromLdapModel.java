package vn.com.ngn.api.user;

import java.util.List;

import lombok.Data;
import vn.com.ngn.api.auth.ApiBelongOrganizationsModel;

@Data
public class ApiResultUserImportFromLdapModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String username;
	private String email;
	private String phone;
	private String fullName;
	private String jobTitle;
	private String creatorId;
	private String creatorName;
	private boolean active;
	private boolean archive;
	private String activeCode;
	private int lastDateLogin;
	private int lastChangePassword;
	private Object lastIPLogin;
	private List<ApiBelongOrganizationsModel> belongOrganizations;
	private Object firstReview;
	private boolean guideWebUI;
	private String provider;
}

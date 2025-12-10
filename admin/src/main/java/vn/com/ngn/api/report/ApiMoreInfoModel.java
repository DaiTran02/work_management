package vn.com.ngn.api.report;

import java.util.List;

import lombok.Data;
import vn.com.ngn.api.auth.ApiBelongOrganizationsModel;

@Data
public class ApiMoreInfoModel {
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
	private List<ApiBelongOrganizationsModel> belongOrganizations;
}

package vn.com.ngn.api.auth;

import java.util.List;

import lombok.Data;

@Data
public class ApiSignInOrgModel {
	private String id;
	private String name;
	private String description;
	private UserExpand userExpand;
	private Roles roles;
	
	
	@Data
	public class UserExpand{
		private String userId;
		private long createdTime;
		private long updatedTime;
		private String userName;
		private Object positionName;
		private Object accountIOffice;
		private boolean active;
		private Object status;
	}
	
	@Data
	public class Roles{
		private List<String> name;
		private List<String> permissionKeys;
	}

}

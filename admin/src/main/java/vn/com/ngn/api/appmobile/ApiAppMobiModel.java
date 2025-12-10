package vn.com.ngn.api.appmobile;

import lombok.Data;

@Data
public class ApiAppMobiModel {
	private String username;
	private String password;
	private String email;
	private String phone;
	private String fullName;
	private boolean active;
}

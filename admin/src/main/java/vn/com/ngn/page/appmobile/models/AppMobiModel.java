package vn.com.ngn.page.appmobile.models;

import lombok.Data;
import vn.com.ngn.api.appmobile.ApiAppMobiModel;

@Data
public class AppMobiModel {
	private String username;
	private String password;
	private String email;
	private String phone;
	private String fullName;
	private boolean active;

	public AppMobiModel(){

	}

	public AppMobiModel(ApiAppMobiModel apiAppMobiModel){
		this.username = apiAppMobiModel.getUsername();
		this.password = apiAppMobiModel.getPassword();
		this.email = apiAppMobiModel.getEmail();
		this.phone = apiAppMobiModel.getPhone();
		this.fullName = apiAppMobiModel.getFullName();
		this.active = apiAppMobiModel.isActive();
	}
}

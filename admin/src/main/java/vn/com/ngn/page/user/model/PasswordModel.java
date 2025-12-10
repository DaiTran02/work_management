package vn.com.ngn.page.user.model;

import lombok.Data;
import vn.com.ngn.api.user.ApiPasswordModel;

@Data
public class PasswordModel {
	private String passwordNew;
	
	public PasswordModel() {
		
	}
	
	public PasswordModel(ApiPasswordModel apiPasswordModel) {
		this.passwordNew = apiPasswordModel.getPasswordNew();
	}
}

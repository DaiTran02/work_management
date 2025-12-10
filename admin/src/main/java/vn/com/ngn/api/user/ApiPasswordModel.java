package vn.com.ngn.api.user;

import lombok.Data;
import vn.com.ngn.page.user.model.PasswordModel;

@Data		
public class ApiPasswordModel {
	private String passwordNew;
	
	public ApiPasswordModel(PasswordModel passwordModel){
		this.passwordNew = passwordModel.getPasswordNew();
	}
}

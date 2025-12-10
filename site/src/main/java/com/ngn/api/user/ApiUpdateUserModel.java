package com.ngn.api.user;

import lombok.Data;

@Data
public class ApiUpdateUserModel {
	private String email;
	private String phone;
	private String fullName;
}

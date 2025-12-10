package com.ngn.api.user;

import lombok.Data;

@Data
public class ApiChangePasswordUserModel {
	private String passwordOld;
	private String passwordNew;
}

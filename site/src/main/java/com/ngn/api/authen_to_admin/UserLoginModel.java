package com.ngn.api.authen_to_admin;

import lombok.Data;

@Data
public class UserLoginModel {
	private String username;
	private String code;
	private long expired;
}

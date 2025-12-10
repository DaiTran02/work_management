package com.ngn.api.sign_in_org;

import java.util.List;

import lombok.Data;

@Data
public class ApiRolesModel {
	private List<String> name;
	private List<String> permissionKeys;

}

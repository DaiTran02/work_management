package com.ngn.api.sign_in_org;

import lombok.Data;

@Data
public class ApiSignInOrgModel {
	private String id;
	private String name;
	private String description;
	private ApiUserExpandModel userExpand;
	private ApiGroupModel group;
	private ApiRolesModel roles;

}

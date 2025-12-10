package com.ngn.models.sign_in_org;

import com.ngn.api.sign_in_org.ApiSignInOrgModel;

import lombok.Data;

@Data
public class SignInOrgModel {
	private String id;
	private String name;
	private String description;
	private UserExpandModel userExpand;
	private GroupModel group = null;
	private RolesModel roles;
	
	public SignInOrgModel() {
		
	}
	
	public SignInOrgModel(ApiSignInOrgModel apiSignInOrgModel) {
		this.id = apiSignInOrgModel.getId();
		this.name = apiSignInOrgModel.getName();
		this.description = apiSignInOrgModel.getDescription();
		this.userExpand = new UserExpandModel(apiSignInOrgModel.getUserExpand());
		this.group = new GroupModel(apiSignInOrgModel.getGroup());
		this.roles = new RolesModel(apiSignInOrgModel.getRoles());
	}
}

package com.ngn.models.sign_in_org;

import java.util.List;

import com.ngn.api.sign_in_org.ApiRolesModel;

import lombok.Data;

@Data
public class RolesModel {
	private List<String> name;
	private List<String> permissionkeys;
	
	public RolesModel() {
		
	}
	
	public RolesModel(ApiRolesModel apiRolesModel) {
		this.name = apiRolesModel.getName();
		this.permissionkeys = apiRolesModel.getPermissionKeys();
	}
}

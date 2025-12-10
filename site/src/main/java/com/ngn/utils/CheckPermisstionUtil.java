package com.ngn.utils;

import com.ngn.api.permission.ApiPermissionFilterModel;
import com.ngn.api.permission.ApiPermissionService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.tdnv.task.enums.PermissionEnum;

public class CheckPermisstionUtil {
	private SignInOrgModel signInOrgModel = SessionUtil.getDetailOrg();
	
	public boolean checkUserIsInGroupOrg() {
		if(signInOrgModel.getGroup() == null || signInOrgModel.getGroup().getId() == null) {
			return false;
		}
		
		return true;
	}
	
	public boolean checkPermissionAddDoc() {
		if(signInOrgModel.getRoles().getPermissionkeys() != null) {
			for(String stringPermiss : signInOrgModel.getRoles().getPermissionkeys()) {
				if(stringPermiss.equals(PermissionEnum.THEMVANBAN.getKey())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	//Xem toan bo van ban don vi
	public boolean checkPermissionViewAllDoc() {
		if(signInOrgModel.getRoles().getPermissionkeys() != null) {
			for(String permiss : signInOrgModel.getRoles().getPermissionkeys()) {
				if(permiss.equals(PermissionEnum.XEMVANBANDONVI.getKey())) {
					return true;
				}
			}
		}
		return false;
	}
	
	//Tổ trưởng tổ giao việc
	public boolean checkPermissionGroupManager() {
		if(signInOrgModel.getRoles().getPermissionkeys() != null) {
			for(String permiss : signInOrgModel.getRoles().getPermissionkeys()) {
				if(permiss.equals(PermissionEnum.QUANLYTOGIAOVIEC.getKey())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkPermissionUserAdministrationDepartment() {
		try {
			ApiPermissionFilterModel apiPermissionFilterModel = new ApiPermissionFilterModel();
			apiPermissionFilterModel.setPermissionKey("quantridonvi");
			apiPermissionFilterModel.setUserId(SessionUtil.getUser().getId());
			ApiResultResponse<Boolean> check = ApiPermissionService.checkUserHasPermission(apiPermissionFilterModel);
			if(check.isSuccess()) {
				return check.getResult();
			}else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

}

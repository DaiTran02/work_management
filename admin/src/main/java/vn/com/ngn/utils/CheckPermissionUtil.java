package vn.com.ngn.utils;

import vn.com.ngn.api.auth.ApiAuthModel;
import vn.com.ngn.api.auth.ApiSignInOrgModel;

public class CheckPermissionUtil {
	
	public boolean checkOrg() {
		if(SessionUtil.getOrgId().equals(SessionUtil.getUser().getId())) {
			return false;
		}
		return true;
	}
	
	public boolean checkPermissionManagerOrg() {
		ApiSignInOrgModel apiSignInOrgModel = SessionUtil.getSignInOrg();
		for(String permiss : apiSignInOrgModel.getRoles().getPermissionKeys()) {
			if(permiss.equals("quantridonvi")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkCreator(String creatorOfData) {
		ApiAuthModel apiAuthModel = SessionUtil.getUser();
		System.out.println(apiAuthModel.getUsername());
		if(creatorOfData.equals(apiAuthModel.getUsername()) || apiAuthModel.getUsername().equals("administrator") || SessionUtil.getUser().getUsername().equals("super_admin")) {
			return true;
		}
		
		if(SessionUtil.getUser().getUsername().equals("administrator") || SessionUtil.getUser().getUsername().equals("super_admin")) {
			return true;
		}
		
		return false;
	}
	
	public boolean checkAdmin() {
		if(SessionUtil.getUser().getUsername().equals("administrator") || SessionUtil.getUser().getUsername().equals("super_admin")) {
			return true;
		}
		return false;
	}

}

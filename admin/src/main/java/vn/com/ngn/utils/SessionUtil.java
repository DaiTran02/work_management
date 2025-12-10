package vn.com.ngn.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.WrappedSession;

import vn.com.ngn.api.auth.ApiAuthModel;
import vn.com.ngn.api.auth.ApiSignInOrgModel;
import vn.com.ngn.page.user.model.BelongOrganizationsModel;

public class SessionUtil {
	public static final String TOKEN = "TOKEN";
	public static final String USER = "USER";
	public static final String ORGID = "ORGID";
	public static final String LISTORG = "LISTORG";
	public static final String DETAILORG = "DETAILORG";
	public static final String SIGNINORG = "SIGNINORG";
	
	
	
	//set section
	public static void setToken(String token) {
		try {
			getSession().setAttribute(TOKEN, token);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setUser(ApiAuthModel apiAuthModel) {
		try {
			getSession().setAttribute(USER, apiAuthModel);
		} catch (Exception e) {
		}
	}
	
	public static void setOrgId(String orgId) {
		try {
			getSession().setAttribute(ORGID, orgId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setListOrg(List<BelongOrganizationsModel> listBelongOrganizationsModels) {
		try {
			getSession().setAttribute(LISTORG, listBelongOrganizationsModels);
		} catch (Exception e) {
		}
	}
	
	public static void setDetailOrg(BelongOrganizationsModel belongOrganizationsModel) {
		try {
			getSession().setAttribute(DETAILORG, belongOrganizationsModel);
		} catch (Exception e) {
		}
	}
	
	public static void setSignInOrg(ApiSignInOrgModel apiSignInOrgModel) {
		try {
			getSession().setAttribute(SIGNINORG, apiSignInOrgModel);
		} catch (Exception e) {
		}
	}
	
	//get section
	public static String getToken() {
		try {
			String token = (String) getAttribute(TOKEN);
			return token;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getOrgId() {
		try {
			String orgId = (String) getAttribute(ORGID);
			return orgId;
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<BelongOrganizationsModel> getListOrg(){
		try {
			return (List<BelongOrganizationsModel>) getAttribute(LISTORG);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static BelongOrganizationsModel getDetailOrg(){
		try {
			return (BelongOrganizationsModel) getAttribute(DETAILORG);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static ApiAuthModel getUser() {
		try {
			return (ApiAuthModel) getAttribute(USER);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static ApiSignInOrgModel getSignInOrg() {
		try {
			return (ApiSignInOrgModel) getAttribute(SIGNINORG);
		} catch (Exception e) {
			return null;
		}
	}
	

	//Support
	public static WrappedSession getSession() {
		return VaadinService.getCurrentRequest().getWrappedSession();
	}
	
	public static Object getAttribute(String attribute) {
		return getSession().getAttribute(attribute);
	}
	
	public static void setAttribute(String key,Object value) {
		getSession().setAttribute(key, value);
	}
	
	public static void removeAttributes(String... keys) {
		for(String key : keys) {
			getSession().removeAttribute(key);
		}
	}
	
	public static void cleanAllSession() {
		Set<String> attributeNames = new HashSet<>(getSession().getAttributeNames());
		attributeNames.forEach(getSession()::removeAttribute);
	}

}

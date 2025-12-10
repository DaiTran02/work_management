package com.ngn.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ngn.enums.DataOfEnum;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;

public class SessionUtil {
	private static Logger logger = LogManager.getLogger(SessionUtil.class);
	
	final static public String TOKEN = "TOKEN";
	final static public String USER = "USER";
	final static public String ORG = "ORG";
	final static public String ORGLIST = "ORGLIST";
	final static public String DETAILORG = "DETAILORG";
	final static public String PARENTORG = "PARENTORG";

	final static public String USERID = "USERID";
	final static public String ORGID = "ORGID";

	final static public String PERMISSION = "PERMISSION";
	final static public String ISPERMISSION = "ISPERMISSION";

	final static public String LEADERTASK = "LEADERTASK";
	final static public String ASSISTTASK = "ASSISTTASK";

	final static public String YEAR = "YEAR";

	final static public String PARAM = "PARAM";

	final static public String ISLOGINBYTOKEN = "ISLOGIN";
	final static public String DATAOFWHO = "DATAOFWHO"; // Check User or Org
	
	public static WrappedSession getSession() {
	    return VaadinSession.getCurrent().getSession();
	}


	public static Object getAttribute(String attribute) {
		return getSession().getAttribute(attribute);
	}

	public static void setAttribute(String key, Object value) {
		getSession().setAttribute(key, value);
	}

	public static void removeAttributes(String... keys) {
		for (String key : keys) {
			getSession().removeAttribute(key);
		}
	}
	
	public static void cleanAllSession() {
		Set<String> attributeNames = new HashSet<>(getSession().getAttributeNames());
		attributeNames.forEach(getSession()::removeAttribute);
	}
	
	
	public static void setToken(String token) {
		try {
			getSession().setAttribute(TOKEN, token);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static String getToken() {
		try {
			return (String) getAttribute(TOKEN);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setIsLoginByToken(boolean checkLogin) {
		try {
			setAttribute(ISLOGINBYTOKEN, checkLogin);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isLoginByToken() {
		try {
			return (boolean) getAttribute(ISLOGINBYTOKEN);
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void setIdUser(String userId) {
		try {
			setAttribute(USERID, userId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static String getIdUser() {
		try {
			return (String) getAttribute(USERID);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setUser(UserAuthenticationModel userModel) {
		try {
			setAttribute(USER, userModel);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static UserAuthenticationModel getUser() {
		try {
			return (UserAuthenticationModel) getAttribute(USER);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void setPermission(List<String> listPermission) {
		try {
			setAttribute(PERMISSION, listPermission);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getPermission(){
		try {
			return (List<String>) getAttribute(PERMISSION);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setPermissionChooseOrg(boolean isPermission) {
		try {
			setAttribute(ISPERMISSION, isPermission);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Boolean isPermissionChooseOrg() {
		try {
			return (Boolean) getAttribute(ISPERMISSION);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void setOrgId(BelongOrganizationModel belongOrganizationModel) {
		try {
			setAttribute(ORGID, belongOrganizationModel.getOrganizationId());
			setAttribute(ORG, belongOrganizationModel);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static String getOrgId() {
		try {
			return (String) getAttribute(ORGID);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static BelongOrganizationModel getOrg() {
		try {
			return (BelongOrganizationModel) getAttribute(ORG);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setBelongOrg(List<BelongOrganizationModel> listOrg) {
		try {
			setAttribute(ORGLIST, listOrg);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static List<BelongOrganizationModel> getBelongOrg(){
		try {
			return (List<BelongOrganizationModel>) getAttribute(ORGLIST);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setParentBelongOrgModel(BelongOrganizationModel belongOrganizationModel) {
		try {
			setAttribute(PARENTORG, belongOrganizationModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static BelongOrganizationModel getParentBelongOrgModel() {
		try {
			return (BelongOrganizationModel) getAttribute(PARENTORG);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void setDetailOrg(SignInOrgModel sinInOrgModel) {
		try {
			setAttribute(DETAILORG, sinInOrgModel);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static SignInOrgModel getDetailOrg() {
		try {
			return (SignInOrgModel) getAttribute(DETAILORG);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void setYear(int year) {
		try {
			setAttribute(YEAR, year);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static int getYear() {
		try {
			return (int) getAttribute(YEAR);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}
	
	public static void setParam(Map<String, String> param) {
		try {
			setAttribute(PARAM, param);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getParam(){
		try {
			return (Map<String, String>) getAttribute(PARAM);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setDataOf(DataOfEnum dataOfEnum) {
		try {
			setAttribute(DATAOFWHO, dataOfEnum);
		} catch (Exception e) {
		}
	}
	
	public static DataOfEnum checkDataOf() {
		try {
			return (DataOfEnum) getAttribute(DATAOFWHO);
		} catch (Exception e) {
			return null;
		}
	}
	
	
}

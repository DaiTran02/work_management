package vn.com.ngn.utils;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class PropUtils implements EnvironmentAware{
	private static Environment env;
	
	public static Object getProperty(String key) {
		return env.getProperty(key);
	}

	@Override
	public void setEnvironment(Environment environment) {
		env = environment;
	}
	
	public static String getCoreApiUrl() {
		return getProperty("apiURL").toString();
	}
	
	public static boolean isCaptchaRequired() {
		return Boolean.parseBoolean(getProperty("login.captcha.required").toString());
	}
	
	// Use these value for login
	public static String getUrlLogoForLogin() {
		String urlLogo = getProperty("login.logo.url") == null ? "./images/logo.png" : getProperty("login.logo.url").toString();
		return urlLogo;
	}
	
	public static String getMainTitlteForLogin() {
		String mainTitle = getProperty("login.main.title").toString();
		if(mainTitle == null) {
			mainTitle = "Hệ thống theo dõi nhiệm vụ"; 
		}else {
			try {
				mainTitle = new String(mainTitle.getBytes("ISO-8859-1"),"UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mainTitle;
	}
	
	// Use these value for mainLayout
	public static String getUrlLogoForMainLayout() {
		String urlLogo = getProperty("main.layout.logo") == null ? "./images/logo.png" : getProperty("main.layout.logo").toString();
		return urlLogo;
	}
	
	public static String getMainTitlteForMainLayout() {
		String mainTitle = getProperty("main.layout.title").toString();
		if(mainTitle == null) {
			mainTitle = "Hệ thống theo dõi nhiệm vụ"; 
		}else {
			try {
				mainTitle = new String(mainTitle.getBytes("ISO-8859-1"),"UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mainTitle;
	}
	
	// Allow app in menu 
	public static boolean isAllowAppAccess() {
		boolean allow = getProperty("main.layout.menu.allow.app.access").toString().equals("false") ?  false :  true;
		return allow;
	}
	
	public static boolean isAllowAppMobile() {
		boolean allow = getProperty("main.layout.menu.allow.app.mobile").toString().equals("false") ?  false :  true;
		return allow;
	}
	
	// Allow create org
	public static boolean isAllowCreateOrg() {
		return getProperty("tndv.admin.org.create").toString().equals("false") ?  false :  true;
	}
	
	// User Manual
	public static String getUrlUserManual() {
		return getProperty("tdnv.admin.main.layout.user.manual.url").toString();
	}
	

}

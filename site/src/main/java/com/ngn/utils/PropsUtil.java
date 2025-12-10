package com.ngn.utils;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class PropsUtil implements EnvironmentAware{
	private static Environment env;
	
	public static Object getProperty(String key) {
		return env.getProperty(key);
	}

	@Override
	public void setEnvironment(Environment environment) {
		env=environment;
	}
	
	public static String getCoreApiUrl() {
		return getProperty("tdnv.core.api.url").toString();
	}
	
	public static boolean isCaptchaRequired() {
		return Boolean.parseBoolean(getProperty("tdnv.login.captcha.required").toString());
	}
	
	
	// Use these value for login
	public static String getUrlLogoInLogin() {
		String urlImage = getProperty("tdnv.login.url.logo") == null ? "/images/logo_normal.png" : getProperty("tdnv.login.url.logo").toString();
		return urlImage;
	}
	
	public static String getMainTitleForLogin() {
		String mainTitle = getProperty("tdnv.login.maintitle").toString();
		if(mainTitle == null) {
			mainTitle = "THEO DÕI NHIỆM VỤ";
		}else {
			try {
				mainTitle = new String(mainTitle.getBytes("ISO-8859-1"),"UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mainTitle;
	}
	
	// Use these value for main-layout
	public static String getUrlLoginForMainLayout() {
		return getProperty("tdnv.main.layout") == null ? "images/logo_normal.png" : getProperty("tdnv.main.layout").toString();
	}
	
	public static String getMainTitleForMainLayout() {
		String mainTitle = getProperty("tdnv.main.layout.title").toString();
		if(mainTitle == null) {
			mainTitle = "THEO DÕI NHIỆM VỤ";
		}else {
			try {
				mainTitle = new String(mainTitle.getBytes("ISO-8859-1"),"UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mainTitle;
	}
	
	// get type file
	
	public static String[] getListTypeFile(){
		String types = getProperty("tdnv.upload.type.file").toString();
		String[] stringType = types.split(",");
		
		return stringType;
	}
	
	// Get URI file 
	
	public static String getURITemplate() {
		return getProperty("tdnv.export.file.template").toString();
	}
	
	//Doc required, Allows document changes when the user wants to change the doc
	public static boolean isChangeDoc() {
		return Boolean.valueOf(getProperty("tdnv.task.doc-required.changes").toString());
	}
	
	//Permission required, Allows role assignment
	public static boolean isPermission() {
		return Boolean.valueOf(getProperty("tdnv.check.permission-required").toString());
	}
	
	//Allow full options don vi
	public static boolean isAllPermission() {
		return Boolean.valueOf(getProperty("tdnv.check.permission.all.roles").toString());
	}
	
	//Info admin
	public static String getNameAdmin() {
		return getProperty("tdnv.info.admin.name").toString();
	}
	
	public static String getPhoneNumberAdmin() {
		return getProperty("tdnv.info.admin.phone").toString();
	}
	
	public static String getEmailAdmin() {
		return getProperty("tdnv.info.admin.email").toString();
	}
	
	//Check required assign
	public static boolean isFollower() {
		return Boolean.valueOf(getProperty("tdnv.assign.follower-required").toString());
	}
	
	// Bookmark
	public static boolean isAllowsBookmark() {
		return Boolean.valueOf(getProperty("tdnv.main.layout.bookmark").toString());
	}
	
	// User manual
	public static String urlHowToUse() {
		return String.valueOf(getProperty("tdnv.main.layout.user.manual.url"));
	}
	
	// Process doc and schedule 
	public static boolean isProcessDocAndSchedule() {
		return Boolean.valueOf(getProperty("tdnv.main.layout.menu.view").toString());
	}
	
	// Allow broadcast of task
	public static boolean isAllowBroadcast() {
		return Boolean.valueOf(getProperty("tdnv.task.broadcast.required").toString());
	}
	
	// Allow authen by OTP
	public static boolean isAllowAuthenByOTP() {
		return Boolean.valueOf(getProperty("tdnv.login.otp.required").toString());
	}
	
	// Allow choose first org
	public static boolean isAllowChooseOrgWhenIsFirstLogin() {
		return Boolean.valueOf(getProperty("tdnv.select.org.required").toString());
	}
	
	// Allow manager tag
	public static boolean isAllowManagerTag() {
		return Boolean.valueOf(getProperty("tdnv.setting.manager.tag.requied").toString());
	}
	
	// Allow personal record
	public static boolean isAllowPersonalRecord() {
		return Boolean.valueOf(getProperty("tdnv.setting.manager.personal.requied").toString());
	}
	
	// Url manager org
	public static String getUrlManagerOrg() {
		return String.valueOf(getProperty("tdnv.main.layout.admin.org"));
	}

}

package com.ngn.captcha;

import java.io.Serializable;

import org.springframework.web.util.WebUtils;

import cn.apiclub.captcha.Captcha;
import jakarta.servlet.http.HttpServletRequest;

public class CaptchaDetails implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private final String answer;
	private final Captcha captcha;
	
	public CaptchaDetails(HttpServletRequest request) {
		this.answer = request.getParameter("answer");
		this.captcha = (Captcha) WebUtils.getSessionAttribute(request, "captcha");
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public Captcha getCaptcha() {
		return captcha;
	}

}

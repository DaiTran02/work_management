package com.ngn.captcha;

import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.apache.tomcat.util.codec.binary.Base64;

import cn.apiclub.captcha.Captcha;

public class CaptchaUtils {
	public static String encodeBase64(Captcha captcha) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(captcha.getImage(), "png", outputStream);
			return Base64.encodeBase64String(outputStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

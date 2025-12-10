package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ws.core.model.RefreshToken;
import ws.core.model.User;
import ws.core.security.JwtTokenProvider;
import ws.core.services.RefreshTokenService;
import ws.core.services.UserService;

@Component
public class RefreshTokenUtil {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	private Document toCommon(RefreshToken oldRefreshToken) {
		User user=userService.getUserById(oldRefreshToken.getUserId());
		RefreshToken newRefreshToken=refreshTokenService.createRefreshToken(user.getId());
		
		Document document=new Document();
		document.put("loginToken", jwtTokenProvider.generateToken(user.getUsername()));
		document.put("refreshToken", newRefreshToken.getRefreshToken());
		document.put("expiryTime", newRefreshToken.getExpiryTimeLong());
		return document;
	}
	
	public Document toAdminResponse(RefreshToken refreshToken) {
		Document document=toCommon(refreshToken);
		return document;
	}
	
	public Document toSiteResponse(RefreshToken refreshToken) {
		Document document=toCommon(refreshToken);
		return document;
	}
}

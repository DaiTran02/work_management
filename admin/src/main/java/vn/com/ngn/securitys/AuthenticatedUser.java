package vn.com.ngn.securitys;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.security.AuthenticationContext;

import jakarta.servlet.ServletException;
import vn.com.ngn.api.auth.ApiAuthModel;
import vn.com.ngn.utils.PropUtils;
import vn.com.ngn.utils.SessionUtil;

@Component
public class AuthenticatedUser {
	private final AuthenticationContext authenticationContext;
	
	public AuthenticatedUser(AuthenticationContext authenticationContext) {
		this.authenticationContext = authenticationContext;
	}
	
	public Optional<ApiAuthModel> get(){
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(principal instanceof UserDetailsCustom) {
			UserDetailsCustom userDetailsCustom = (UserDetailsCustom) principal;
			return Optional.ofNullable(userDetailsCustom.getUser());
		}
		
		return Optional.empty();
	}
	
	public void logout() {
		SessionUtil.cleanAllSession();
		authenticationContext.logout();
	}
	
	public boolean isAuthenticated() {
		VaadinServletRequest request = VaadinServletRequest.getCurrent();
		return request != null && request.getUserPrincipal() != null;
	}
	
	public boolean authenticate(String username,String password) {
	       VaadinServletRequest request = VaadinServletRequest.getCurrent();
	        if (request == null) {
	            return false;
	        }
	        try {
	            request.login(username, password);
	            return true;
	        } catch (ServletException e) {
	            return false;
	        }
	}
	
	public boolean authenticateByCode(String code) {
		if(isAuthenticated()) {
			authenticationContext.logout();
		}
		
		VaadinServletRequest vaadinServletRequest = VaadinServletRequest.getCurrent();
		try {
			vaadinServletRequest.login(null, code);
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	public boolean isCaptchaRequired() {
		return PropUtils.isCaptchaRequired();
	}
}

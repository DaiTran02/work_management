package vn.com.ngn.securitys;

import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import vn.com.ngn.api.auth.ApiAuthModel;
import vn.com.ngn.api.auth.ApiAuthService;
import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.page.user.model.BelongOrganizationsModel;
import vn.com.ngn.utils.SessionUtil;

@Component
public class AuthenticationProviderCustom implements AuthenticationProvider{

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		
		if(username != null && password != null && !username.isEmpty()) {
			try {
				ApiResultResponse<ApiAuthModel> data = ApiAuthService.login(username, password);
				if(data.getStatus()==200) {
					ApiAuthModel loginModel = data.getResult();
					
					UserDetailsCustom userDetailsCustom = new UserDetailsCustom();
					userDetailsCustom.setUser(loginModel);
					SessionUtil.setToken(loginModel.getLoginToken());
					
					SessionUtil.setUser(loginModel);
					
					if(loginModel.getBelongOrganizations().isEmpty()) {
						SessionUtil.setOrgId(loginModel.getId());
					}else {
						if(loginModel.getBelongOrganizations().size() == 1) {
							System.out.println("Check");
							SessionUtil.setOrgId(loginModel.getBelongOrganizations().get(0).getOrganizationId());
							SessionUtil.setDetailOrg(new BelongOrganizationsModel(loginModel.getBelongOrganizations().get(0)));
						}
						
						SessionUtil.setListOrg(loginModel.getBelongOrganizations().stream().map(BelongOrganizationsModel::new).collect(Collectors.toList()));
					}
					
					return new UsernamePasswordAuthenticationToken(userDetailsCustom, password,userDetailsCustom.getAuthorities());
				}else {
					SessionUtil.cleanAllSession();
					System.out.println("Authentication failed");
//					throw new BadCredentialsException("Authentication failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			try {
				ApiResultResponse<ApiAuthModel> data = ApiAuthService.loginByCode(password);
				if(data.isSuscces()) {
					ApiAuthModel loginModel = data.getResult();
					UserDetailsCustom userDetailsCustom = new UserDetailsCustom();
					userDetailsCustom.setUser(loginModel);
					SessionUtil.setToken(loginModel.getLoginToken());
					
					SessionUtil.setUser(loginModel);
					return new UsernamePasswordAuthenticationToken(userDetailsCustom, password,userDetailsCustom.getAuthorities());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}

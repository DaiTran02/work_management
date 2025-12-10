package com.ngn.secutity;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.ngn.api.authentication.ApiAuthenticationModel;
import com.ngn.api.authentication.ApiAuthenticationService;
import com.ngn.api.permission.ApiPermissionFilterModel;
import com.ngn.api.permission.ApiPermissionService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.NotificationTemplate;

@Component
public class AuthenticationProviderCustom implements AuthenticationProvider{
	private Logger log = LogManager.getLogger(AuthenticationProviderCustom.class);

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		if(username != null && password != null && !username.isEmpty()) {
			try {
				ApiResultResponse<ApiAuthenticationModel> dataUser = ApiAuthenticationService.login(username,password);
				if(dataUser.isSuccess()) {
					ApiAuthenticationModel loginModel = dataUser.getResult();

					UserDetailsCustom userDetailsCustom = new UserDetailsCustom();
					userDetailsCustom.setUser(loginModel);
					SessionUtil.setIdUser(loginModel.getId());
					SessionUtil.setToken(loginModel.getLoginToken());
					UserAuthenticationModel userModel = new UserAuthenticationModel(loginModel);
					SessionUtil.setUser(userModel);
					SessionUtil.setBelongOrg(userModel.getBelongOrganizations());
					SessionUtil.setYear(0);
					if(userModel.getBelongParentOrganizations() != null && !userModel.getBelongParentOrganizations().isEmpty()) {
						SessionUtil.setParentBelongOrgModel(userModel.getBelongParentOrganizations().get(0));
					}

					ApiPermissionFilterModel apiPermissionFilterModel = new ApiPermissionFilterModel();
					apiPermissionFilterModel.setUserId(loginModel.getId());
					apiPermissionFilterModel.setPermissionKey("theodoihethong");
					ApiResultResponse<Boolean> checkPermission = ApiPermissionService.checkUserHasPermission(apiPermissionFilterModel);
					if(checkPermission.isSuccess()) {
						SessionUtil.setPermissionChooseOrg(checkPermission.getResult());
					}

					if(checkPermission.getResult() == false) {
						if(userModel.getBelongOrganizations().size() == 1) {
							SessionUtil.setOrgId(userModel.getBelongOrganizations().get(0));
						}
					}

					return new UsernamePasswordAuthenticationToken(userDetailsCustom, password, userDetailsCustom.getAuthorities());
				}

				else {
					SessionUtil.cleanAllSession();
					NotificationTemplate.error(dataUser.getMessage());
					throw new BadCredentialsException("User authentication failed!!!");
				}
			} catch (IOException e) {

				e.printStackTrace();
				log.debug(e.getMessage());
				throw new BadCredentialsException(e.getMessage());

			} catch(Exception e) {
				NotificationTemplate.error("Vui lòng liên hệ với quản trị viên");
				e.printStackTrace();
				log.debug(e.getMessage());
				throw new BadCredentialsException(e.getMessage());
			}
		}else {
			try {
				ApiAuthenticationModel apiAuthenticationModel = checkShortTermToken(password);

				if(!apiAuthenticationModel.getId().isEmpty()) {
					SessionUtil.setIdUser(apiAuthenticationModel.getId());
					SessionUtil.setToken(apiAuthenticationModel.getLoginToken());
					UserAuthenticationModel userModel = new UserAuthenticationModel(apiAuthenticationModel);
					SessionUtil.setUser(userModel);
					SessionUtil.setBelongOrg(userModel.getBelongOrganizations());
					SessionUtil.setYear(0);
					SessionUtil.setIsLoginByToken(true);

					UserDetailsCustom userDetailsCustom = new UserDetailsCustom();
					userDetailsCustom.setUser(apiAuthenticationModel);

					SessionUtil.setIsLoginByToken(true);

					ApiPermissionFilterModel apiPermissionFilterModel = new ApiPermissionFilterModel();
					apiPermissionFilterModel.setUserId(userModel.getId());
					apiPermissionFilterModel.setPermissionKey("theodoihethong");
					SessionUtil.setPermissionChooseOrg(false);

					if(userModel.getBelongOrganizations().size() == 1) {
						SessionUtil.setOrgId(userModel.getBelongOrganizations().get(0));
					}

					return new UsernamePasswordAuthenticationToken(userDetailsCustom, password, userDetailsCustom.getAuthorities());
				}else {
					SessionUtil.cleanAllSession();
					throw new BadCredentialsException("User authentication failed!!!");
				}

			} catch (Exception e) {
				log.debug(e.getMessage());
				throw new BadCredentialsException(e.getMessage());
			}
		}


	}

	private ApiAuthenticationModel checkShortTermToken(String token) {
		ApiAuthenticationModel authenModel = null;
		ApiResultResponse<ApiAuthenticationModel> apiAuthenticationModel = null;
		try {
			apiAuthenticationModel = ApiAuthenticationService.loginByCode(token);
			authenModel = apiAuthenticationModel.getResult();
			System.out.println(apiAuthenticationModel.getMessage());
		} catch (Exception e) {
			System.out.println(apiAuthenticationModel.getMessage());
			e.printStackTrace();
		}
		return authenModel;
	}

	public boolean checkShortTermTokenSeccond(String token) {
		ApiResultResponse<ApiAuthenticationModel> apiAuthenticationModel = null;
		try {
			apiAuthenticationModel = ApiAuthenticationService.loginByCode(token);
			if(apiAuthenticationModel.isSuccess()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}

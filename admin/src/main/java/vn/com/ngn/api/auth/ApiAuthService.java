package vn.com.ngn.api.auth;

import java.io.IOException;

import org.springframework.core.ParameterizedTypeReference;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.services.CoreExchangeService;

public class ApiAuthService {
	public static final String LOGIN = "api/admin/users/login";
	
	public static ApiResultResponse<ApiAuthModel> login(String username,String password) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		ApiAuthModel authModel = new ApiAuthModel();
		authModel.setUsername(username);
		authModel.setPassword(password);
		return coreExchangeService.postUser(LOGIN, new ParameterizedTypeReference<ApiResultResponse<ApiAuthModel>>() {}, authModel);
	}
	
	public static ApiResultResponse<ApiSignInOrgModel> signInOrg(String idOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get("api/site/users/sign-in-organization/"+idOrg, new ParameterizedTypeReference<ApiResultResponse<ApiSignInOrgModel>>() {});
	}
	
	public static ApiResultResponse<ApiAuthModel> loginByCode(String code) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		ApiCodeInputModel apiCodeInputModel = new ApiCodeInputModel();
		apiCodeInputModel.setCode(code);
		return coreExchangeService.postUser("api/admin/users/login-by-code", new ParameterizedTypeReference<>() {}, apiCodeInputModel);
	}

}

package com.ngn.api.authentication;

import java.io.IOException;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.services.CoreExchangeService;

public class ApiAuthenticationService {
	private static final String LOGIN = "/api/site/users/";
	
	public static ApiResultResponse<ApiAuthenticationModel> login(String username,String password) throws IOException{
		ApiAccountInputModel apiAccountInputModel = new ApiAccountInputModel();
		apiAccountInputModel.setUsername(username);
		apiAccountInputModel.setPassword(password);
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.postAuthen(LOGIN+"login", new ParameterizedTypeReference<ApiResultResponse<ApiAuthenticationModel>>() {}, apiAccountInputModel);
	}
	
	public static ApiResultResponse<ApiAuthenticationModel> loginByCode(String code) throws IOException{
		ApiCodeInputModel apiCodeInputModel = new ApiCodeInputModel();
		apiCodeInputModel.setCode(code);
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.postAuthen(LOGIN+"login-by-code", new ParameterizedTypeReference<ApiResultResponse<ApiAuthenticationModel>>() {}, apiCodeInputModel);
	}

}

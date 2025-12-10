package com.ngn.api.authen_to_admin;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.services.CoreExchangeService;
import com.ngn.utils.SessionUtil;

public class AuthenToAdminService {
	public static ApiResultResponse<String> generateApiKey(){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get("/api/general-x-api-key", new ParameterizedTypeReference<>() {});
	}
	
	public static ApiResultResponse<UserLoginModel> generateShortToken(String apiKey){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		UserLoginModel userLoginModel = new UserLoginModel();
		userLoginModel.setUsername(SessionUtil.getUser().getUsername());
		return coreExchangeService.postByApiKey("/api/partner/v1/users-code/generate",apiKey, new ParameterizedTypeReference<>() {}, userLoginModel);
	}
	
}

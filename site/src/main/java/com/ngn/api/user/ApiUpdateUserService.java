package com.ngn.api.user;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.services.CoreExchangeService;

public class ApiUpdateUserService {
	private static final String USER = "/api/site/users/";
	
	public static ApiResultResponse<Object> updateUser(String idUser,ApiUpdateUserModel apiUpdateUserModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(USER+idUser, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiUpdateUserModel);
	}
	
	public static ApiResultResponse<Object> changePassword(String idUser,ApiChangePasswordUserModel apiChangePasswordUserModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(USER+idUser+"/change-password", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiChangePasswordUserModel);
	}
	
	public static ApiResultResponse<Object> guidedUi(String idUser){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(USER+idUser+"/set-guided-webui", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, null);
	}

}

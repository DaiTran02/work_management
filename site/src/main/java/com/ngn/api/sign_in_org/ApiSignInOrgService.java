package com.ngn.api.sign_in_org;

import java.io.IOException;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.services.CoreExchangeService;

public class ApiSignInOrgService {
	private static String ORG = "/api/site/users";
	
	public static ApiResultResponse<ApiSignInOrgModel> getDetailOrg(String idOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/sign-in-organization/"+idOrg, new ParameterizedTypeReference<ApiResultResponse<ApiSignInOrgModel>>() {});
	}

}

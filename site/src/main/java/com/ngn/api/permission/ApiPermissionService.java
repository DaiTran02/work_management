package com.ngn.api.permission;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.services.CoreExchangeService;

public class ApiPermissionService {
	public static ApiResultResponse<Boolean>checkUserHasPermission(ApiPermissionFilterModel apiPermissionFilterModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiPermissionFilterModel);
		return coreExchangeService.get("/api/site/permissions/check-user-has-permission?"+param, new ParameterizedTypeReference<>() {});
	}
}

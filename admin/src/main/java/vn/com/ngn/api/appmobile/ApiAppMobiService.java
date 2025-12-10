package vn.com.ngn.api.appmobile;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.services.CoreExchangeService;

public class ApiAppMobiService {
	private static final String APP = "api/admin/app-mobies";
	
	public static ApiResultResponse<List<ApiAppMobiModel>> getListDevices(int skip,int limit) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(APP+"?skip="+skip+"&limit="+limit, new ParameterizedTypeReference<ApiResultResponse<List<ApiAppMobiModel>>>() {});
	}

}

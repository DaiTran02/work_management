package vn.com.ngn.api.app_access;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.services.CoreExchangeService;

public class ApiAppAccessService {
	private static final String ACCESS = "api/admin/app-accesses";
	
	public static ApiResultResponse<List<ApiAppAccessModel>> getListAppAccess(int skip,int limit,String checkActive,String keyword) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String search = "";
		if(checkActive != null) {
			search = ACCESS+"?skip="+skip+"&limit="+limit+"&active="+checkActive+"&keyword="+keyword;
		}else {
			search = ACCESS+"?skip="+skip+"&limit="+limit+"&keyword="+keyword;
		}
		
		return coreExchangeService.get(search, new ParameterizedTypeReference<ApiResultResponse<List<ApiAppAccessModel>>>() {});
	}
	
	public static ApiResultResponse<ApiAppAccessModel> getOneAppAccess(String appAccessId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ACCESS+"/"+appAccessId, new ParameterizedTypeReference<ApiResultResponse<ApiAppAccessModel>>() {});
	}
	
	public static ApiResultResponse<Object> createAppAccess(ApiAppAccessModel apiAppAccessModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(ACCESS, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiAppAccessModel);
	}
	
	public static ApiResultResponse<Object> updateAppAccess(String appAccessId,ApiAppAccessModel apiAppAccessModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(ACCESS+"/"+appAccessId, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiAppAccessModel);
	}
	
	public static ApiResultResponse<Object> deleteAppAccess(String appAccessId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(ACCESS+"/"+appAccessId, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
  
}

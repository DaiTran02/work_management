package com.ngn.api.personal_record;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.services.CoreExchangeService;

public class ApiPersonalService {
	private static String API = "/api/site/personal";
	
	public static ApiResultResponse<List<ApiPersonalRecordModel>>getAllpersonalByFilter(ApiPersonalFilter apiPersonalFilter){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiPersonalFilter);
		return coreExchangeService.get(API+"?"+param, new ParameterizedTypeReference<>() {});
	}
	
	public static ApiResultResponse<Object> createPersonal(ApiPersonalRecordModel apiPersonalRecordModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(API, new ParameterizedTypeReference<>() {}, apiPersonalRecordModel);
	}
	
	public static ApiResultResponse<Object> updatePersonal(String id,ApiPersonalRecordModel apiPersonalRecordModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/"+id, new ParameterizedTypeReference<>() {}, apiPersonalRecordModel);
	}
	
	public static ApiResultResponse<Object> deletePersonal(String id){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(API+"/"+id, new ParameterizedTypeReference<>() {});
	}
	
	public static ApiResultResponse<ApiPersonalDetailModel> getDetailPersonal(String id){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/detail/"+id, new ParameterizedTypeReference<>() {});
	}
	
	public static ApiResultResponse<ApiPersonalRecordModel> getOnePersonal(String id){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+id, new ParameterizedTypeReference<>() {});
	}
	
	public static ApiResultResponse<Object> transferPersonal(String personalId,String userId){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/transfer/"+personalId+"/userId/"+userId, new ParameterizedTypeReference<>() {},null);
	}
	
	public static ApiResultResponse<List<ApiPersonalRecordModel>> getListOldPersonal(ApiPersonalFilter apiPersonalFilter){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiPersonalFilter);
		return coreExchangeService.get(API+"/transfer?"+param, new ParameterizedTypeReference<>() {});
	}
	
	public static ApiResultResponse<Object> putMoreDataDocOrTask(String id,ApiPersonalRecordModel apiPersonalRecordModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/add/"+id, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiPersonalRecordModel);
	}
	
	public static ApiResultResponse<List<ApiPersonalRecordModel>> getListPersonalTransferred(ApiPersonalFilter apiPersonalFilter){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiPersonalFilter);
		return coreExchangeService.get(API+"/transferred?"+param, new ParameterizedTypeReference<>() {});
	}
	
}

package com.ngn.api.classify_task;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.services.CoreExchangeService;
import com.ngn.setting.leader_classify.model.FilterClassifyLeaderModel;

public class ApiClassifyTaskService {
	private static final String API = "/api/site/classify-task";
	
	public static ApiResultResponse<Object> createClassify(ApiClassifyTaskModel apiClassifyTaskModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(API, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiClassifyTaskModel);
	}
	
	public static ApiResultResponse<List<ApiClassifyTaskModel>> getListClassify (FilterClassifyLeaderModel filterClassifyLeaderModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(filterClassifyLeaderModel);
		return coreExchangeService.get(API+"?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiClassifyTaskModel>>>() {});
	}
	
	public static ApiResultResponse<ApiClassifyTaskModel> getAClassify(String idClassify){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idClassify, new ParameterizedTypeReference<ApiResultResponse<ApiClassifyTaskModel>>() {});
	}
	
	public static ApiResultResponse<Object> updateClassify(String idClassify,ApiClassifyTaskModel apiClassifyTaskModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/"+idClassify, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiClassifyTaskModel);
	}
	
	public static ApiResultResponse<Object> deleteClassify(String idClassify){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(API+"/"+idClassify, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}

}

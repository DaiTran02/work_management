package com.ngn.api.tags;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.services.CoreExchangeService;

public class ApiTagService {
	private static final String API = "/api/site/tags";
	
	public static ApiResultResponse<List<ApiTagModel>> getListTags(ApiTagFilterModel apiTagFilterModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiTagFilterModel);
		return coreExchangeService.get(API+"?"+param, new ParameterizedTypeReference<>() {});
	}
	
	public static ApiResultResponse<ApiTagModel> getTag(String idTag){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idTag, new ParameterizedTypeReference<>() {});
	}
	
	public static ApiResultResponse<Object> createTag(ApiTagModel apiTagModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(API, new ParameterizedTypeReference<>() {}, apiTagModel);
	}
	
	public static ApiResultResponse<Object> updateTag(String idTag,ApiTagModel apiTagModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/"+idTag, new ParameterizedTypeReference<>() {}, apiTagModel);
	}
	
	public static ApiResultResponse<Object> deleteTag(String idTag){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(API+"/"+idTag, new ParameterizedTypeReference<>() {});
	}
	
	public static ApiResultResponse<List<ApiKeyValueModel>> getTypeFilter(){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/get-types-filter", new ParameterizedTypeReference<ApiResultResponse<List<ApiKeyValueModel>>>() {});
	}
	
	public static ApiResultResponse<ApiTagModel> addClass(ApiInputAddclassModel apiInputAddclassModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/add-class", new ParameterizedTypeReference<ApiResultResponse<ApiTagModel>>() {}, apiInputAddclassModel);
	}
	
	public static ApiResultResponse<ApiTagModel> removeClass(ApiInputAddclassModel apiInputAddclassModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/remove-class", new ParameterizedTypeReference<ApiResultResponse<ApiTagModel>>() {}, apiInputAddclassModel);
	}
	
}

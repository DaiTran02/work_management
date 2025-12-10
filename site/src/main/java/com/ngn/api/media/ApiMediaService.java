package com.ngn.api.media;

import java.io.IOException;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.services.CoreExchangeService;

public class ApiMediaService {
	private static final String MEDIA = "/api/site/medias";
	
	public static ApiResultResponse<ApiMediaModel> createFile(ApiInputMediaModel apiInputMediaModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.postFile(MEDIA, new ParameterizedTypeReference<ApiResultResponse<ApiMediaModel>>() {}, apiInputMediaModel);
	}
	
	public static ApiResultResponse<Object> deleteFile(String idFile) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(MEDIA+"/"+idFile, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<ApiMediaModel> getInfoFile(String idFile) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(MEDIA+"/"+idFile, new ParameterizedTypeReference<ApiResultResponse<ApiMediaModel>>() {});
	}
	
	public static ApiResultResponse<String> getContentFile(String idFile) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(MEDIA+"/"+idFile+"/get-content", new ParameterizedTypeReference<ApiResultResponse<String>>() {});
	}
}

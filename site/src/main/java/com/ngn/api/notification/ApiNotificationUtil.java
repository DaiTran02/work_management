package com.ngn.api.notification;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.services.CoreExchangeService;

@Component
public class ApiNotificationUtil implements ApiNotificationService{
	private final String NOTIFI = "/api/site/notifications/";

	@Override
	public ApiResultResponse<List<ApiNotificationModel>> getAllNotifi(ApiNotifiFilterModel apiNotifiFilterModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiNotifiFilterModel);
		return coreExchangeService.get(NOTIFI+"list?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiNotificationModel>>>() {});
	}

	@Override
	public ApiResultResponse<Object> countNotifi(ApiNotifiFilterModel apiNotifiFilterModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiNotifiFilterModel);
		return coreExchangeService.get(NOTIFI+"count?"+param, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}

	@Override
	public ApiResultResponse<ApiNotificationModel> getInfoNotifi(String idNotifi) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(NOTIFI+idNotifi, new ParameterizedTypeReference<>() {} );
	}

	@Override
	public ApiResultResponse<List<ApiNotifiKeyValueModel>> getScope() {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(NOTIFI+"scope", new ParameterizedTypeReference<>() {});
	}

	@Override
	public ApiResultResponse<List<ApiNotifiKeyValueModel>> getType() {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(NOTIFI+"type", new ParameterizedTypeReference<>() {});
	}

	@Override
	public ApiResultResponse<List<ApiNotifiActionModel>> getAction() {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(NOTIFI+"action", new ParameterizedTypeReference<>() {});
	}

	@Override
	public ApiResultResponse<List<ApiNotifiKeyValueModel>> getObject() {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(NOTIFI+"object", new ParameterizedTypeReference<>() {});
	}

	@Override
	public ApiResultResponse<Object> setMarkViewed(String notifiId) {
		CoreExchangeService  coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(NOTIFI+notifiId+"/mark-viewed", new ParameterizedTypeReference<>() {}, null);
	}

	@Override
	public ApiResultResponse<Object> setAllMarkViewed(ApiNotifiFilterModel apiNotifiFilterModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiNotifiFilterModel);
		return coreExchangeService.put(NOTIFI+"mark-all-viewed?"+param, new ParameterizedTypeReference<>() {}, null);
	}
	
	

}

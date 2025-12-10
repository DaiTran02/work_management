package com.ngn.api.notification;

import java.util.List;

import com.ngn.api.result.ApiResultResponse;

public interface ApiNotificationService {
	ApiResultResponse<List<ApiNotificationModel>> getAllNotifi(ApiNotifiFilterModel apiNotifiFilterModel);
	ApiResultResponse<Object> countNotifi (ApiNotifiFilterModel apiNotifiFilterModel);
	ApiResultResponse<ApiNotificationModel> getInfoNotifi(String idNotifi);
	ApiResultResponse<List<ApiNotifiKeyValueModel>> getScope();
	ApiResultResponse<List<ApiNotifiKeyValueModel>> getType();
	ApiResultResponse<List<ApiNotifiActionModel>> getAction();
	ApiResultResponse<List<ApiNotifiKeyValueModel>> getObject();
	ApiResultResponse<Object> setMarkViewed(String notifiId);
	ApiResultResponse<Object> setAllMarkViewed(ApiNotifiFilterModel apiNotifiFilterModel);
}

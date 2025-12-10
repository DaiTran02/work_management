package com.ngn.api.leader_approve_task;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.services.CoreExchangeService;
import com.ngn.setting.leader_classify.model.FilterClassifyLeaderModel;

public class ApiLeaderApproveTaskService {
	private static final String API = "/api/site/leader-approve-task";
	
	public static ApiResultResponse<Object> createLeader(ApiLeaderApproveTaskModel apiLeaderApproveTaskModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(API, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiLeaderApproveTaskModel);
	}
	
	public static ApiResultResponse<List<ApiLeaderApproveTaskModel>> getListLeader(FilterClassifyLeaderModel filterClassifyLeaderModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(filterClassifyLeaderModel);
		return coreExchangeService.get(API+"?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiLeaderApproveTaskModel>>>() {});
	}
	
	public static ApiResultResponse<ApiLeaderApproveTaskModel> getALeader(String idLeader){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idLeader, new ParameterizedTypeReference<ApiResultResponse<ApiLeaderApproveTaskModel>>() {});
	}
	
	public static ApiResultResponse<Object> updateLeader(String idLeader,ApiLeaderApproveTaskModel apiLeaderApproveTaskModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/"+idLeader, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiLeaderApproveTaskModel);
	}
	
	public static ApiResultResponse<Object> deleteLeader(String idLeader){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(API+"/"+idLeader, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}

}

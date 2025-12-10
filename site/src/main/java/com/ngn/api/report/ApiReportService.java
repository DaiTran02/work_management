package com.ngn.api.report;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.services.CoreExchangeService;

public class ApiReportService {
	private static final String API = "/api/site";
	
	public static ApiResultResponse<List<ApiReportDocModel>> getReportDoc(ApiFilterReportDocModel apiFilterReportDocModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterReportDocModel);
		return coreExchangeService.get(API+"/report/list-docs?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiReportDocModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiListTasksOwnerModel>> getReportTaskOwner(ApiFilterReportTaskModel apiFilterReportTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterReportTaskModel);
		return coreExchangeService.get(API+"/report/list-tasks-owner?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiListTasksOwnerModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiListTaskSupportModel>> getReportTaskSupport(ApiFilterReportTaskModel apiFilterReportTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterReportTaskModel);
		return coreExchangeService.get(API+"/tasks/list-tasks-support?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiListTaskSupportModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiListTaskAssigneeModel>> getReportTaskAssignee(ApiFilterReportTaskModel apiFilterReportTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterReportTaskModel);
		return coreExchangeService.get(API+"/tasks/list-tasks-assignee?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiListTaskAssigneeModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiListTaskFollowerModel>> getReportTaskFollower(ApiFilterReportTaskModel apiFilterReportTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterReportTaskModel);
		return coreExchangeService.get(API+"/report/list-tasks-follower?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiListTaskFollowerModel>>>() {});
	}
	
	public static ApiResultResponse<ApiReportKpiModel> getReportKpi(ApiFilterReportTaskKpiModel apiFilterReportTaskKpiModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterReportTaskKpiModel);
		return coreExchangeService.get(API+"/report/list-tasks-kpi?"+param, new ParameterizedTypeReference<>() {});
	}
	
	
}

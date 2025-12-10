package com.ngn.api.tasks;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.services.CoreExchangeService;

public class ApiTaskService {
	
	private static final String API = "/api/site";
	
	// Event Task
	
	public static ApiResultResponse<ApiInputTaskModel> createTask(ApiInputTaskModel apiInputTaskModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(API+"/tasks", new ParameterizedTypeReference<ApiResultResponse<ApiInputTaskModel>>() {}, apiInputTaskModel);
	}
	
	public static ApiResultResponse<ApiInputTaskModel> updateTask(String idTask,ApiInputTaskModel apiInputTaskModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask, new ParameterizedTypeReference<ApiResultResponse<ApiInputTaskModel>>() {}, apiInputTaskModel);
	}
	
	public static ApiResultResponse<ApiOutputTaskModel> getAtask(String idTask) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/tasks/"+idTask, new ParameterizedTypeReference<ApiResultResponse<ApiOutputTaskModel>>() {});
	}
	
	public static ApiResultResponse<Object> doDelete(String idTask){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(API+"/tasks/"+idTask, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<Object> createChildTask(String idTask,ApiInputTaskModel apiInputTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(API+"/tasks/"+idTask, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiInputTaskModel);
	}
	
	public static ApiResultResponse<List<ApiOutputTaskModel>> getListSubTask(String idParentTask){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/tasks/"+idParentTask+"/sub-tasks", new ParameterizedTypeReference<ApiResultResponse<List<ApiOutputTaskModel>>>() {});
	}
	
	public static ApiResultResponse<Object> doRefuseConfirmTask(String idTask,ApiTaskRefuseConfirmModel taskRefuseConfirmModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-confirm-refuse", new ParameterizedTypeReference<>() {}, taskRefuseConfirmModel);
	}
	
	//Owner
	
	public static ApiResultResponse<List<ApiOutputTaskModel>> getListTaskOwner(ApiFilterTaskModel apiFilterTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/list-tasks-owner?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiOutputTaskModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiTaskSummaryModel>> getSummaryTasksOwner(ApiFilterTaskModel apiFilterTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/summary-tasks-owner?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiTaskSummaryModel>>>() {});
	}
	
	public static ApiResultResponse<Object> countTaskOwner(ApiFilterTaskModel apiFilterTaskModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/count-tasks-owner?"+param, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<List<ApiNameAndValueModel>> getListAchivementTaskOwner(ApiFilterAchivementModel achivementModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(achivementModel);
		return coreExchangeService.get(API+"/tasks/achivement-tasks-owner?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiNameAndValueModel>>>() {});
	}
	
	//Assignee
	
	public static ApiResultResponse<List<ApiOutputTaskModel>> getListAssignee(ApiFilterTaskModel apiFilterTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/list-tasks-assignee?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiOutputTaskModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiTaskSummaryModel>> getSummaryTaskAssignee(ApiFilterTaskModel apiFilterTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/summary-tasks-assignee?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiTaskSummaryModel>>>() {});
	}
	
	public static ApiResultResponse<Object> countTaskAssignee(ApiFilterTaskModel apiFilterTaskModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/count-tasks-assignee?"+param, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<List<ApiNameAndValueModel>> getListAchivementTaskAssignee(ApiFilterAchivementModel achivementModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(achivementModel);
		return coreExchangeService.get(API+"/tasks/achivement-tasks-assignee?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiNameAndValueModel>>>() {});
	}
	
	//Support
	
	public static ApiResultResponse<List<ApiOutputTaskModel>> getListSupport(ApiFilterTaskModel apiFilterTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		System.out.println("Param ne: "+API+"/tasks/list-tasks-support?"+param);
		return coreExchangeService.get(API+"/tasks/list-tasks-support?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiOutputTaskModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiTaskSummaryModel>> getSummaryTaskSupport(ApiFilterTaskModel apiFilterTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/summary-tasks-support?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiTaskSummaryModel>>>() {});
	}
	
	public static ApiResultResponse<Object> countTaskSupport(ApiFilterTaskModel apiFilterTaskModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/count-tasks-support?"+param, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<List<ApiNameAndValueModel>> getListAchivementTaskSupport(ApiFilterAchivementModel achivementModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(achivementModel);
		return coreExchangeService.get(API+"/tasks/achivement-tasks-support?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiNameAndValueModel>>>() {});
	}
	
	//follower
	
	public static ApiResultResponse<List<ApiOutputTaskModel>> getListFollower(ApiFilterTaskModel apiFilterTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/list-tasks-follower?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiOutputTaskModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiTaskSummaryModel>> getSummaryTasksFollower(ApiFilterTaskModel apiFilterTaskModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/summary-tasks-follower?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiTaskSummaryModel>>>() {});
	}
	
	public static ApiResultResponse<Object> countTaskFollower(ApiFilterTaskModel apiFilterTaskModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskModel);
		return coreExchangeService.get(API+"/tasks/count-tasks-follower?"+param, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<List<ApiNameAndValueModel>> getListAchivementTaskFollower(ApiFilterTaskModel achivementModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(achivementModel);
		return coreExchangeService.get(API+"/tasks/achivement-tasks-follower?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiNameAndValueModel>>>() {});
	}
	
	// Filter
	
	public static ApiResultResponse<List<ApiKeyValueModel>> getStatus (){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/tasks/status", new ParameterizedTypeReference<ApiResultResponse<List<ApiKeyValueModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiKeyValueModel>> getCategory(){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/tasks/category", new ParameterizedTypeReference<ApiResultResponse<List<ApiKeyValueModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiTaskSourceModel>> getScource(){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/tasks/source", new ParameterizedTypeReference<ApiResultResponse<List<ApiTaskSourceModel>>>() {});
	}
	
}

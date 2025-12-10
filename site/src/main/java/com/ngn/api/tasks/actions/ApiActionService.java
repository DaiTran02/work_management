package com.ngn.api.tasks.actions;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.services.CoreExchangeService;

// TODO: Auto-generated Javadoc
/**
 * The Class ApiActionService.
 */
public class ApiActionService {
	
	/** The Constant API. */
	private static final String API = "/api/site";
	
	/**
	 * Do refuse.
	 *
	 * @param idTask the id task
	 * @param apiRefuseModel the api refuse model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doRefuse(String idTask,ApiRefuseModel apiRefuseModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-refuse", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiRefuseModel);
	}
	
	/**
	 * Do accept.
	 *
	 * @param idTask the id task
	 * @param apiAcceptModel the api accept model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doAccept(String idTask,ApiAcceptModel apiAcceptModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-accept", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiAcceptModel);
	}
	
	/**
	 * Do completed.
	 *
	 * @param idTask the id task
	 * @param apiCompletedModel the api completed model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doCompleted(String idTask,ApiCompletedInputModel apiCompletedModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-completed", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiCompletedModel);
	}
	
	/**
	 * Do reverse.
	 *
	 * @param idTask the id task
	 * @param apiReverseModel the api reverse model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doReverse(String idTask,ApiReverseModel apiReverseModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-reverse", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiReverseModel);
	}
	
	/**
	 * Do report.
	 *
	 * @param idTask the id task
	 * @param apiReportModel the api report model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doReport(String idTask,ApiReportModel apiReportModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-report", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiReportModel);
	}
	
	/**
	 * Do confirm.
	 *
	 * @param idTask the id task
	 * @param apiConfirmModel the api confirm model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doConfirm(String idTask,ApiConfirmModel apiConfirmModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-confirm", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiConfirmModel);
	}
	
	
	public static ApiResultResponse<Object> doChangeConfirm(){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API, null, null);
	}
	
	/**
	 * Do pedding.
	 *
	 * @param idTask the id task
	 * @param apiPedingModel the api peding model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doPedding(String idTask,ApiPedingModel apiPedingModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-pending", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiPedingModel);
	}
	
	/**
	 * Do un pendding.
	 *
	 * @param idTask the id task
	 * @param apiUnpendingModel the api unpending model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doUnPendding(String idTask,ApiUnpendingModel apiUnpendingModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-unpending", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiUnpendingModel);
	}
	
	/**
	 * Do redo.
	 *
	 * @param idTask the id task
	 * @param apiRedoModel the api redo model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doRedo(String idTask,ApiRedoModel apiRedoModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-redo", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiRedoModel);
	}
	
	/**
	 * Do rating.
	 *
	 * @param idTask the id task
	 * @param apiRatingModel the api rating model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doRating(String idTask,ApiRatingModel apiRatingModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-rating", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiRatingModel);
	}
	
	/**
	 * Do remind.
	 *
	 * @param idTask the id task
	 * @param apiRemindModel the api remind model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doRemind(String idTask,ApiRemindModel apiRemindModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-remind", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiRemindModel);
	}
	
	/**
	 * Do update process.
	 *
	 * @param idTask the id task
	 * @param apiUpdateProcessModel the api update process model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doUpdateProcess(String idTask,ApiUpdateProcessModel apiUpdateProcessModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-update-process", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiUpdateProcessModel);
	}
	
	/**
	 * Do comment.
	 *
	 * @param idTask the id task
	 * @param apiCommentModel the api comment model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doComment(String idTask,ApiCommentModel apiCommentModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-comment", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiCommentModel);
	}
	
	/**
	 * Do reply comment.
	 *
	 * @param idTask the id task
	 * @param idParentCommendId the id parent commend id
	 * @param apiCommentModel the api comment model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doReplyComment(String idTask,String idParentCommendId,ApiCommentModel apiCommentModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-reply-comment/"+idParentCommendId, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiCommentModel);
	}
	
	/**
	 * Do assign user assignee.
	 *
	 * @param idTask the id task
	 * @param orgId the org id
	 * @param apiDoAssignModel the api do assign model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doAssignUserAssignee(String idTask,String orgId,ApiDoAssignModel apiDoAssignModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		System.out.println(API+"/tasks/"+idTask+"/do-assign-user-assignee/"+orgId);
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-assign-user-assignee/"+orgId, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiDoAssignModel);
	}
	
	/**
	 * Do assign user support.
	 *
	 * @param idTask the id task
	 * @param orgId the org id
	 * @param apiDoAssignModel the api do assign model
	 * @return the api result response
	 */
	public static ApiResultResponse<Object> doAssignUserSupport(String idTask,String orgId,ApiDoAssignModel apiDoAssignModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-assign-user-support/"+orgId, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiDoAssignModel);
	}
	
	public static ApiResultResponse<Object> doRedoAndReportAgain(String idTask,ApiCreatorDoRedoAndReportAgainModel apiCreatorActionModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"/tasks/"+idTask+"/do-redo-and-report-again", new ParameterizedTypeReference<>() {}, apiCreatorActionModel);
	}
	
}

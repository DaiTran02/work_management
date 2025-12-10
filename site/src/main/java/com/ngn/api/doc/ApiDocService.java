package com.ngn.api.doc;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.organization.ApiGroupExpandModel;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskSummaryModel;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.api.utils.ApiFileModel;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.services.CoreExchangeService;

public class ApiDocService {
	private static final String DOC = "/api/site/docs";
	private static final String GROUP = "/api/site/organizations";
	private static final String API = "/api/site";
	
	// Request Doc
	public static ApiResultResponse<Object> countDoc (ApiFilterListDocModel apiFilterListDocModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterListDocModel);
		return coreExchangeService.get(DOC+"/count?" + param, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	public static ApiResultResponse<List<ApiDocModel>> getListDoc(ApiFilterListDocModel apiFilterListDocModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterListDocModel);
		return coreExchangeService.get(DOC+"?" + param, new ParameterizedTypeReference<ApiResultResponse<List<ApiDocModel>>>() {});
	}
	
	public static ApiResultResponse<ApiDocModel> getAdoc(String idDoc){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(DOC+"/"+idDoc, new ParameterizedTypeReference<ApiResultResponse<ApiDocModel>>() {});
	}
	
	public static ApiResultResponse<ApiDocModel> getAdocByOfficeId(String iofficeid){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(DOC+"/get-by-iofficeid/"+iofficeid, new ParameterizedTypeReference<ApiResultResponse<ApiDocModel>>() {});
	}
	
	public static ApiResultResponse<ApiDocModel> createDoc(ApiDocInputModel apiDocInputModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(DOC, new ParameterizedTypeReference<ApiResultResponse<ApiDocModel>>() {}, apiDocInputModel);
	}
	
	public static ApiResultResponse<Object> updateDoc(String idDoc,ApiDocInputModel apiDocInputModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(DOC+"/"+idDoc, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiDocInputModel);
	}
	
	public static ApiResultResponse<Object> deleteDoc(String idDoc) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(DOC+"/"+idDoc, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<Object> addAttachmentOfDoc(String idDoc, ApiFileModel apiFileModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.postFile(DOC+"/"+idDoc+"/add-attachment", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiFileModel);
	}
	
	public static ApiResultResponse<Object> removeAttachmentOfDoc(String idDoc,String idAttachment) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(DOC+"/"+idDoc+"/delete-attachment/"+idAttachment, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<List<ApiGroupExpandModel>> getListGroup(String idOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(GROUP+"/"+idOrg+"/list-groups", new ParameterizedTypeReference<ApiResultResponse<List<ApiGroupExpandModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiUserGroupExpandModel>> getListUserOfGroup(String idOrg,String idGroup) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(GROUP+"/"+idOrg+"/list-users-in-group/"+idGroup, new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGroupExpandModel>>>() {});
	}
	
	public static ApiResultResponse<Object> completeDoc(String idDoc,ApiDocCompletedModel apiDocCompletedModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put("/api/site/docs/"+idDoc+"/confirm-complete", new ParameterizedTypeReference<>() {}, apiDocCompletedModel);
	}
	
	// Request doc filter
	public static ApiResultResponse<List<ApiKeyValueModel>> getKeyValueCategory(){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(DOC+"/category", new ParameterizedTypeReference<ApiResultResponse<List<ApiKeyValueModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiKeyValueModel>> getKeyValueStatus() throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(DOC+"/status", new ParameterizedTypeReference<ApiResultResponse<List<ApiKeyValueModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiKeyValueModel>> getSecurity() throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(DOC+"/security", new ParameterizedTypeReference<ApiResultResponse<List<ApiKeyValueModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiKeyValueModel>> getPriority(){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/tasks/priority", new ParameterizedTypeReference<ApiResultResponse<List<ApiKeyValueModel>>>() {});
	}
	
	// Request doc attachment
	public static ApiResultResponse<String> getAttachmentOfDoc(String idDoc,String filePath) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(DOC+"/"+idDoc+"/get-attachment?filePath="+filePath, new ParameterizedTypeReference<ApiResultResponse<String>>() {});
	}
	
	// Request task doc
	public static ApiResultResponse<List<ApiTaskSummaryModel>> getSummaryTaskOfDoc(ApiFilterSummaryDocModel apiFilterSummaryDocModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterSummaryDocModel);
		return coreExchangeService.get(API+"/tasks/summary-tasks-delivered-by-doc?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiTaskSummaryModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiOutputTaskModel>> getListTaskOfDoc(ApiFilterTaskOfDocModel apiFilterTaskOfDocModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterTaskOfDocModel);
		return coreExchangeService.get(API+"/tasks/list-tasks-delivered-by-doc?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiOutputTaskModel>>>() {});
	}
	
	// Request Tree Tasks
	public static ApiResultResponse<List<ApiDocTreeTaskModel>> getTreeDocTask(String idDoc){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(DOC+"/"+idDoc+"/get-tree-tasks", new ParameterizedTypeReference<ApiResultResponse<List<ApiDocTreeTaskModel>>>() {});
	}
	
	public static ApiResultResponse<ApiDataSummaryModel> getSummary(ApiFilterListDocModel apiFilterListDocModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String search = ApiConvertUtil.convertToParams(apiFilterListDocModel);
		return coreExchangeService.get(DOC+"/summary?"+search, new ParameterizedTypeReference<>() {});
	}
}

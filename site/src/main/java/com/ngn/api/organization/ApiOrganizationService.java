package com.ngn.api.organization;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.services.CoreExchangeService;

// TODO: Auto-generated Javadoc
/**
 * The Class ApiOrganizationService.
 */
public class ApiOrganizationService{
	
	/** The Constant API. */
	private static final String API = "/api/site/organizations";
	
	/** The Constant CAREGORIS. */
	private static final String CATEGORIES = "/api/site";

	/**
	 * Gets the list user organization ex.
	 *
	 * @param orgId the org id
	 * @return the list user organization ex
	 */
	public static ApiResultResponse<List<ApiUserGroupExpandModel>> getListUserOrganizationEx(String orgId){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+orgId+"/list-users", new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGroupExpandModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiUserGroupExpandModel>> getListUserForOwnerTask(String orgId){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+orgId+"/list-users-for-owner-task", new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGroupExpandModel>>>() {});
	}
	
	/**
	 * Gets the list organization.
	 *
	 * @param idPanrent the id panrent
	 * @return the list organization
	 */
	public static ApiResultResponse<List<ApiOrganizationModel>> getListOrganization(String idPanrent){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String url = "?skip=0&limit=0";
		if(idPanrent != null) {
			url += "&parentId="+idPanrent;
		}
		return coreExchangeService.get(API+url, new ParameterizedTypeReference<ApiResultResponse<List<ApiOrganizationModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiOrganizationModel>> getListOrg(ApiFilterOrgModel apiFilterOrgModel){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterOrgModel);
		return coreExchangeService.get(API+"?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiOrganizationModel>>>() {});
	}
	
	/**
	 * Gets the list group.
	 *
	 * @param idOrg the id org
	 * @return the list group
	 */
	public static ApiResultResponse<List<ApiGroupExpandModel>> getListGroup (String idOrg,String keySearch){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idOrg+"/list-groups?keyword="+keySearch, new ParameterizedTypeReference<ApiResultResponse<List<ApiGroupExpandModel>>>() {});
	}
	
	/**
	 * Gets the list user group.
	 *
	 * @param idOrg the id org
	 * @param idGroup the id group
	 * @return the list user group
	 */
	public static ApiResultResponse<List<ApiUserGroupExpandModel>> getListUserGroup(String idOrg,String idGroup){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idOrg+"/list-users-in-group/"+idGroup, new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGroupExpandModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiUserGroupExpandModel>> getListUserNotInGroup(String idOrg){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idOrg+"/list-users-not-in-all-group", new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGroupExpandModel>>>() {});
	}
	
	/**
	 * Gets the one org.
	 *
	 * @param idOrg the id org
	 * @return the one org
	 */
	public static ApiResultResponse<ApiOrganizationModel> getOneOrg(String idOrg){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idOrg,new ParameterizedTypeReference<ApiResultResponse<ApiOrganizationModel>>() {});
	}
	
	/**
	 * Gets the list org categories.
	 *
	 * @return the list org categories
	 */
	public static ApiResultResponse<List<ApiCategoriesOrgModel>> getListOrgCategories(){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(CATEGORIES+"/organization-categories", new ParameterizedTypeReference<ApiResultResponse<List<ApiCategoriesOrgModel>>>() {});
	}
	
	/**
	 * Gets the one org categories.
	 *
	 * @param idCategoriesOrg the id categories org
	 * @return the one org categories
	 */
	public static ApiResultResponse<ApiCategoriesOrgModel> getOneOrgCategories(String idCategoriesOrg){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(CATEGORIES+"/organization-categories/"+idCategoriesOrg, new ParameterizedTypeReference<ApiResultResponse<ApiCategoriesOrgModel>>() {});
	}

	public static ApiResultResponse<List<ApiSubOrganizationRootModel>> getOrgTreeRoot(){
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/trees-root", new ParameterizedTypeReference<ApiResultResponse<List<ApiSubOrganizationRootModel>>>() {});
	}

	
}

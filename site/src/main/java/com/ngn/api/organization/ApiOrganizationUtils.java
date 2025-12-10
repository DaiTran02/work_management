package com.ngn.api.organization;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.utils.ApiConvertUtil;
import com.ngn.services.CoreExchangeService;

@Service
public class ApiOrganizationUtils implements ApiOrganizationServiceCustom{
	
	/** The Constant API. */
	private static final String API = "/api/site/organizations";

	@Override
	public ApiResultResponse<List<ApiOrganizationModel>> getListOrg(ApiFilterOrgModel apiFilterOrgModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterOrgModel);
		return coreExchangeService.get(API+"?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiOrganizationModel>>>() {});
	}

	@Override
	public ApiResultResponse<List<ApiUserGroupExpandModel>> getListUserOfOrg(String idParent) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idParent+"/list-users", new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGroupExpandModel>>>() {});
	}

	@Override
	public ApiResultResponse<List<ApiRoleOfOrgModel>> getListRoles(String idOrg) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idOrg+"/list-roles", new ParameterizedTypeReference<ApiResultResponse<List<ApiRoleOfOrgModel>>>() {});
	}

	@Override
	public ApiResultResponse<List<ApiPermissionModel>> getPermissions() {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get("/api/site/permissions", new ParameterizedTypeReference<ApiResultResponse<List<ApiPermissionModel>>>() {});
	}

	@Override
	public ApiResultResponse<Object> addToOrg(String idUser,ApiAddToOrgModel apiAddToOrgModel) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put("/api/site/users/"+idUser+"/add-to-organization", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiAddToOrgModel);
	}

	@Override
	public ApiResultResponse<List<ApiGroupExpandModel>> getListGroup(String idOrg) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idOrg+"/list-groups", new ParameterizedTypeReference<ApiResultResponse<List<ApiGroupExpandModel>>>() {});
	}

	@Override
	public ApiResultResponse<List<ApiUserGroupExpandModel>> getListUsersOfGroup(String idOrg, String idGroup) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idOrg+"/list-users-in-group/"+idGroup, new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGroupExpandModel>>>() {});
	}

	@Override
	public ApiResultResponse<List<ApiUsersRoleModel>> getListUsersOfRole(String idOrg, String idRole) {
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"/"+idOrg+"/list-users-in-role/"+idRole, new ParameterizedTypeReference<ApiResultResponse<List<ApiUsersRoleModel>>>() {});
	}

}

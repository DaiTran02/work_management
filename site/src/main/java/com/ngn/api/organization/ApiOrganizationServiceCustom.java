package com.ngn.api.organization;

import java.util.List;

import com.ngn.api.result.ApiResultResponse;

public interface ApiOrganizationServiceCustom {
	public ApiResultResponse<List<ApiOrganizationModel>> getListOrg(ApiFilterOrgModel apiFilterOrgModel);
	public ApiResultResponse<List<ApiUserGroupExpandModel>> getListUserOfOrg(String idParent);
	public ApiResultResponse<List<ApiRoleOfOrgModel>> getListRoles(String idOrg);
	public ApiResultResponse<List<ApiPermissionModel>> getPermissions();
	public ApiResultResponse<Object> addToOrg(String idUser,ApiAddToOrgModel apiAddToOrgModel);
	public ApiResultResponse<List<ApiGroupExpandModel>> getListGroup(String idOrg);
	public ApiResultResponse<List<ApiUserGroupExpandModel>> getListUsersOfGroup(String idOrg,String idGroup);
	public ApiResultResponse<List<ApiUsersRoleModel>> getListUsersOfRole(String idOrg,String idRole);
}

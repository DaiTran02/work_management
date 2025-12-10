package vn.com.ngn.api.organization;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.page.organization.models.ListUserIDsModel;
import vn.com.ngn.services.CoreExchangeService;

public class ApiOrganizationService {
	private static final String ORG = "api/admin/organizations";
	private static final String USER = "api/admin/users";
	private static final String PERMISSION = "api/admin/permissions";

	public static ApiResultResponse<List<ApiOrganizationModel>> getListOrganization(int skip,int limit,String parentId,String keyword,String active) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = "";
		if(parentId==null) {
			if(active == null) {
				param +="?skip="+skip+"&limit="+limit+"&keyword="+keyword;
			}else {
				param +="?skip="+skip+"&limit="+limit+"&keyword="+keyword+"&active="+active;
			}

		}else {
			if(active == null) {
				param += "?skip="+skip+"&limit="+limit+"&parentId="+parentId+"&keyword="+keyword;
			}else {
				param += "?skip="+skip+"&limit="+limit+"&parentId="+parentId+"&keyword="+keyword+"&active="+active;
			}
		}


		return coreExchangeService.get(ORG+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiOrganizationModel>>>() {});
	}

	public static ApiResultResponse<List<ApiUserOrganizationExpandsModel>> getListUserOrg(String id) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/"+id+"/list-users", new ParameterizedTypeReference<ApiResultResponse<List<ApiUserOrganizationExpandsModel>>>() {});
	}

	public static ApiResultResponse<ApiOrganizationModel> getOneOrg(String idOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/"+idOrg, new ParameterizedTypeReference<ApiResultResponse<ApiOrganizationModel>>() {});
	}

	public static ApiResultResponse<ApiOrganizationModel> createNewOrg(ApiCreateAndUpdateOrgModel createOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(ORG, new ParameterizedTypeReference<ApiResultResponse<ApiOrganizationModel>>() {}, createOrg);
	}

	public static ApiResultResponse<Object> updateOrg(String id,ApiCreateAndUpdateOrgModel updateOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(ORG+"/"+id, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, updateOrg);
	}

	public static ApiResultResponse<Object> deleteOrg(String id) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(ORG+"/"+id, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}

	//Request Group Organization Expands

	public static ApiResultResponse<List<ApiGroupOrganizationExpandsModel>> getListGroup(String id) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/"+id+"/list-groups", new ParameterizedTypeReference<ApiResultResponse<List<ApiGroupOrganizationExpandsModel>>>() {});
	}

	public static ApiResultResponse<ApiGroupOrganizationExpandsModel> getAGroup(String parentId,String groupId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/"+parentId+"/get-group/"+groupId, new ParameterizedTypeReference<ApiResultResponse<ApiGroupOrganizationExpandsModel>>() {});
	}

	public static ApiResultResponse<Object> createGroup(String id,ApiGroupOrganizationExpandsModel apiGroupOrganizationExpandsModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(ORG+"/"+id+"/create-group", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiGroupOrganizationExpandsModel);
	}

	public static ApiResultResponse<Object> updateGroup(String parentId,String idGroup,ApiGroupOrganizationExpandsModel apiGroupOrganizationExpandsModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(ORG+"/"+parentId+"/update-group/"+idGroup, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiGroupOrganizationExpandsModel);
	}

	public static ApiResultResponse<Object> deleteGroup(String parentId,String idGroup) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(ORG+"/"+parentId+"/delete-group/"+idGroup, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}

	public static ApiResultResponse<List<ApiUserGeneraModel>> getListUserNotInAllGroup(String parentId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/"+parentId+"/list-users-not-in-all-group", new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGeneraModel>>>() {});
	}

	public static ApiResultResponse<List<ApiUserGeneraModel>> getListUserInGroup(String parentId,String idGroup) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/"+parentId+"/list-users-in-group/"+idGroup, new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGeneraModel>>>() {});
	}

	//Request User Organization Expands

	public static ApiResultResponse<List<ApiUserModel>> getListUserExcludeOrg(String excludeOrgID,String keyword) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(USER+"?skip=0&limit=10000&keyword="+keyword+"&excludeOrganizationId="+excludeOrgID, new ParameterizedTypeReference<ApiResultResponse<List<ApiUserModel>>>() {});
	}

	public static ApiResultResponse<List<ApiUserModel>> getListUserIncludeOrg(String includeOrgID,String keyword) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(USER+"?skip=0&limit=10000&keyword="+keyword+"&includeOrganizationId="+includeOrgID, new ParameterizedTypeReference<ApiResultResponse<List<ApiUserModel>>>() {});
	}

	public static ApiResultResponse<Object> moveUsersToOrg(String parentId,ListUserIDsModel listUserIDsModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(ORG+"/"+parentId+"/add-users", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, listUserIDsModel);
	}

	public static ApiResultResponse<Object> removeUsersFormOrg(String parentId,ListUserIDsModel listUserIDsModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.deleteHaveObject(ORG+"/"+parentId+"/remove-users", new ParameterizedTypeReference<ApiResultResponse<Object>>() {},listUserIDsModel);
	}

	public static ApiResultResponse<ApiUserGeneraModel> getAUserOfOrg(String parentId,String userId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/"+parentId+"/get-user/"+userId, new ParameterizedTypeReference<ApiResultResponse<ApiUserGeneraModel>>() {});
	}

	public static ApiResultResponse<Object> updateUserOfOrg(String parentId,String userId,ApiUserGeneraModel apiUserGeneraModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(ORG+"/"+parentId+"/update-user/"+userId, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiUserGeneraModel);
	}

	public static ApiResultResponse<Object> deleteUserOfOrg(String parentId,ListUserIDsModel listUserIDsModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.deleteHaveObject(ORG+"/"+parentId+"/remove-users", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, listUserIDsModel);
	}

	//Request Role Organization Expands

	public static ApiResultResponse<List<ApiRoleOrganizationExpandsModel>> getListRole(String parentId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/"+parentId+"/list-roles", new ParameterizedTypeReference<ApiResultResponse<List<ApiRoleOrganizationExpandsModel>>>() {});
	}

	public static ApiResultResponse<ApiRoleOrganizationExpandsModel> getOneRole(String parentId,String roleId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/"+parentId+"/get-role/"+roleId, new ParameterizedTypeReference<ApiResultResponse<ApiRoleOrganizationExpandsModel>>() {});
	}

	public static ApiResultResponse<Object> createRole(String parentId,ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(ORG+"/"+parentId+"/create-role", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiRoleOrganizationExpandsModel);
	}

	public static ApiResultResponse<Object> updateRole(String parentId,String roleId,ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(ORG+"/"+parentId+"/update-role/"+roleId, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiRoleOrganizationExpandsModel);
	}

	public static ApiResultResponse<Object> deleteRole(String parentId,String roleId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(ORG+"/"+parentId+"/delete-role/"+roleId, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}

	public static ApiResultResponse<List<ApiUserGeneraModel>> getListUserInRole(String parentId,String roleId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(ORG+"/"+parentId+"/list-users-in-role/"+roleId, new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGeneraModel>>>() {});
	}

	public static ApiResultResponse<List<ApiUserGeneraModel>> getListUserNotInRole(String parentId,String roleId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();

		return coreExchangeService.get(ORG+"/"+parentId+"/list-users-not-in-role/"+roleId, new ParameterizedTypeReference<ApiResultResponse<List<ApiUserGeneraModel>>>() {});
	}

	public static ApiResultResponse<List<ApiPermissionModel>> getListPermision () throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(PERMISSION, new ParameterizedTypeReference<ApiResultResponse<List<ApiPermissionModel>>>() {});
	}

	//Request Role template

	public static ApiResultResponse<List<ApiRoleOrganizationExpandsModel>> getListRoleTemplate(int skip,int limit,String keySearch) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get("api/admin/role-template/list?skip="+skip+"&limit="+limit+"&keyword="+keySearch, new ParameterizedTypeReference<ApiResultResponse<List<ApiRoleOrganizationExpandsModel>>>() {});
	}

	public static ApiResultResponse<ApiRoleOrganizationExpandsModel> getOneRoleTemplate(String roleId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get("api/admin/role-template/get/"+roleId, new ParameterizedTypeReference<ApiResultResponse<ApiRoleOrganizationExpandsModel>>() {});
	}

	public static ApiResultResponse<Object> createRoleTemplate(ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post("api/admin/role-template/create", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiRoleOrganizationExpandsModel);
	}

	public static ApiResultResponse<Object> updateRoleTemplate(String roleId,ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put("api/admin/role-template/update/"+roleId, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiRoleOrganizationExpandsModel);
	}

	public static ApiResultResponse<Object> deleteRoleTemplate(String roleId) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete("api/admin/role-template/delete/"+roleId, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<List<ApiKeyAndValueOrgModel>> getLevels() throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get("api/admin/organizations/get-level", new ParameterizedTypeReference<>() {});
	}

}






















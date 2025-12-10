package vn.com.ngn.api.user;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.utils.ApiConvertUtil;
import vn.com.ngn.page.user.model.UserModel;
import vn.com.ngn.services.CoreExchangeService;

public class ApiUserService {
	private static final String USER = "api/admin/users";
	
	public static ApiResultResponse<List<ApiUserModel>> getAllUser(ApiUserFilter apiUserFilter) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiUserFilter);
		return coreExchangeService.get(USER+"?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiUserModel>>>() {});
	}
	
	public static ApiResultResponse<ApiUserModel> getaUser(String id) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(USER+"/"+id, new ParameterizedTypeReference<ApiResultResponse<ApiUserModel>>() {});
	}
	
	public static ApiResultResponse<ApiUserModel> createUser(UserModel userModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(USER, new ParameterizedTypeReference<ApiResultResponse<ApiUserModel>>() {}, userModel);
	}
	
	public static ApiResultResponse<Object> updateUser(String id,UserModel userModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(USER+"/"+id, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, userModel);
	}
	
	public static ApiResultResponse<Object>deleteUser(String id) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(USER+"/"+id, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<Object>resetPassword(String id,ApiPasswordModel apiPassword) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(USER+"/"+id+"/reset-password", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiPassword);
	}
	
	public static ApiResultResponse<Object>setReviewOfFirstLogin(String idUser) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(USER+"/"+idUser+"/set-reviewed-of-first-review", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, null);
	}
	
	public static ApiResultResponse<List<ApiResultUserImportFromLdapModel>> importUserToSystemFromLdap(ApiImportUsersLdapToSystemModel apiImportUsersLdapToSystemModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(USER+"/import-from-ldap", new ParameterizedTypeReference<>() {}, apiImportUsersLdapToSystemModel);
	}

}

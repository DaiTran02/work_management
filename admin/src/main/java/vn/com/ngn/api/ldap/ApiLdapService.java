package vn.com.ngn.api.ldap;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.utils.ApiConvertUtil;
import vn.com.ngn.services.CoreExchangeService;

public class ApiLdapService {
	private static final String LDAP = "/api/admin/ldap";
	
	public static ApiResultResponse<List<ApiListUserLdapModel>> searchUsers(ApiUserFilterLdapModel apiUserFilterLdapModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String params = ApiConvertUtil.convertToParams(apiUserFilterLdapModel);
		return coreExchangeService.get(LDAP+"/search-users?"+params, new ParameterizedTypeReference<>() {});
	}
}

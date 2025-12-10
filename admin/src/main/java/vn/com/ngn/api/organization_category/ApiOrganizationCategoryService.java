package vn.com.ngn.api.organization_category;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.utils.ApiConvertUtil;
import vn.com.ngn.services.CoreExchangeService;

public class ApiOrganizationCategoryService {
	private static final String API = "/api/admin/";
	
	public static ApiResultResponse<List<ApiOrganizationCategoryModel>> getListOrganizationCategory(ApiOrganizationCategoryFilterModel apiOrganizationCategoryFilterModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiOrganizationCategoryFilterModel);
		return coreExchangeService.get(API+"organization-categories?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiOrganizationCategoryModel>>>() {});
	}
	
	public static ApiResultResponse<ApiOrganizationCategoryModel> getAOrg(String idOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get(API+"organization-categories/"+idOrg, new ParameterizedTypeReference<ApiResultResponse<ApiOrganizationCategoryModel>>() {});
	}
	
	public static ApiResultResponse<Object> updateOrgCate(String idOrg,ApiOrganizationCategoryModel apiOrganizationCategoryModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(API+"organization-categories/"+idOrg, new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiOrganizationCategoryModel);
	}
	
	public static ApiResultResponse<Object> deleteOrgCate(String idOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.delete(API+"organization-categories/"+idOrg, new ParameterizedTypeReference<ApiResultResponse<Object>>() {});
	}
	
	public static ApiResultResponse<Object> createOrg(ApiOrganizationCategoryModel apiOrganizationCategoryModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(API+"organization-categories", new ParameterizedTypeReference<ApiResultResponse<Object>>() {}, apiOrganizationCategoryModel);
	}
}


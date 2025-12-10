package vn.com.ngn.api.report;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.utils.ApiConvertUtil;
import vn.com.ngn.services.CoreExchangeService;

public class ApiReportService {
	private static final String REPORT = "api/admin/report/";
	private static final String ORG = "api/admin/organizations";
	
	public static ApiResultResponse<List<ApiOrganizationForReportModel>> getListOrganization(ApiOrganizationReportFilterModel apiOrganizationReportFilterModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiOrganizationReportFilterModel);
		return coreExchangeService.get(REPORT+"list-organizations?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiOrganizationForReportModel>>>() {});
	}
	
	public static ApiResultResponse<List<ApiOrganizationModel>> getListOrgByIdParent(ApiOrganizationReportFilterModel apiOrganizationReportFilterModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiOrganizationReportFilterModel);
		return coreExchangeService.get(ORG+"?"+param, new ParameterizedTypeReference<>() {});
	}
	
	public static ApiResultResponse<List<ApiUserSystemModel>> getListUsersSystem(ApiFilterReportOrgSystemModel apiFilterReportOrgSystemModel) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String param = ApiConvertUtil.convertToParams(apiFilterReportOrgSystemModel);
		return coreExchangeService.get(REPORT+"list-users-system?"+param, new ParameterizedTypeReference<ApiResultResponse<List<ApiUserSystemModel>>>() {});
	}
	
	public static ApiResultResponse<ApiOrganizationForReportModel> getListUsersUsing(String organizationId,boolean includeSub,String status,long fromDate,long toDate) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String query = "";
		if(status == null) {
			query += REPORT+"list-users-using?organizationId="+organizationId+"&includeSub="+includeSub+"&fromDate="+fromDate+"&toDate="+toDate;
		}else {
			query += REPORT+"list-users-using?organizationId="+organizationId+"&includeSub="+includeSub+"&status="+status+"&fromDate="+fromDate+"&toDate="+toDate;
		}
		
		return coreExchangeService.get(query, new ParameterizedTypeReference<ApiResultResponse<ApiOrganizationForReportModel>>() {});
	}

}

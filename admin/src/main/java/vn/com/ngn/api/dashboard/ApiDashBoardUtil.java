package vn.com.ngn.api.dashboard;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.services.CoreExchangeService;

@Component
public class ApiDashBoardUtil implements ApiDashBoardService{

	@Override
	public ApiResultResponse<List<ApiDashBoardUserModel>> listUserDashBoard(String idOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get("/api/admin/dashboards/users", new ParameterizedTypeReference<ApiResultResponse<List<ApiDashBoardUserModel>>>() {});
	}

	@Override
	public ApiResultResponse<List<ApiDashBoardOrgModel>> listOrgDashBoard(String idOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.get("/api/admin/dashboards/organizations", new ParameterizedTypeReference<ApiResultResponse<List<ApiDashBoardOrgModel>>>() {});
	}

}

package vn.com.ngn.api.dashboard;

import java.io.IOException;
import java.util.List;

import vn.com.ngn.api.exchange.ApiResultResponse;

public interface ApiDashBoardService {
	ApiResultResponse<List<ApiDashBoardUserModel>> listUserDashBoard(String idOrg) throws IOException;
	ApiResultResponse<List<ApiDashBoardOrgModel>> listOrgDashBoard(String idOrg) throws IOException;
}

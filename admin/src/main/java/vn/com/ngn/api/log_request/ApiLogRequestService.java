package vn.com.ngn.api.log_request;

import java.io.IOException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.services.CoreExchangeService;

public class ApiLogRequestService {
	private static final String LOG = "api/admin/log-requests";
	
	public static ApiResultResponse<List<ApiLogRequestModel>> getListLogRequest(int skip,int limit,long fromDate,long toDate,String keyWord) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		String query = "";
		if(!keyWord.isEmpty()) {
			query = LOG+"?skip="+skip+"&limit="+limit+"&fromDate="+fromDate+"&toDate="+toDate+"&keyword="+keyWord;
		}else {
			query = LOG+"?skip="+skip+"&limit="+limit+"&fromDate="+fromDate+"&toDate="+toDate;
		}
		return coreExchangeService.get(query, new ParameterizedTypeReference<ApiResultResponse<List<ApiLogRequestModel>>>() {});
	}
	
}

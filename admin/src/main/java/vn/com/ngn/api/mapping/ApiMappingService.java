package vn.com.ngn.api.mapping;

import java.io.IOException;

import org.springframework.core.ParameterizedTypeReference;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.services.CoreExchangeService;

public class ApiMappingService {
	private static final String URI = "api/admin/mapping/org";
	
	public static ApiResultResponse<String> mappingAllOrg() throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.post(URI, new ParameterizedTypeReference<>() {}, null);
	}
	
	public static ApiResultResponse<String> mappingByIdOrg(String idOrg) throws IOException{
		CoreExchangeService coreExchangeService = new CoreExchangeService();
		return coreExchangeService.put(URI+"/"+idOrg, new ParameterizedTypeReference<>() {}, null);
	}
	
}

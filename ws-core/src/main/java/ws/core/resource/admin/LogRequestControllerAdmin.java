package ws.core.resource.admin;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.LogRequest;
import ws.core.model.filter.LogRequestFilter;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.LogRequestUtil;
import ws.core.services.LogRequestService;

@RestController
@RequestMapping("/api/admin")
public class LogRequestControllerAdmin {

	@Autowired
	private LogRequestService logRequestService;
	
	@Autowired
	private LogRequestUtil logRequestUtil;
	
	@GetMapping("/log-requests")
	public Object list(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		LogRequestFilter logRequestFilter=new LogRequestFilter();
		logRequestFilter.setFromDate(fromDate);
		logRequestFilter.setToDate(toDate);
		logRequestFilter.setKeySearch(keyword);
		
		OrderByFilter orderByFilter=new OrderByFilter();
		orderByFilter.add("createdTime", Direction.DESC);
		logRequestFilter.setOrderByFilter(orderByFilter);
		logRequestFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=logRequestService.countLogRequestAll(logRequestFilter);
		List<LogRequest> logRequests=logRequestService.findLogRequestAll(logRequestFilter);
		List<Document> results=new ArrayList<Document>();
		for (LogRequest item : logRequests) {
			results.add(logRequestUtil.toAdminResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/log-requests/{id}")
	public Object getByKey(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		LogRequest logRequest=logRequestService.findLogRequestById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(logRequestUtil.toAdminResponse(logRequest));
		return responseAPI.build();
	}
}

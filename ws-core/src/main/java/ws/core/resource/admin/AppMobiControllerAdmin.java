package ws.core.resource.admin;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.AppMobi;
import ws.core.model.filter.AppMobiFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.AppMobiUtil;
import ws.core.services.AppMobiService;

@RestController
@RequestMapping("/api/admin")
public class AppMobiControllerAdmin {
	
	@Autowired
	private AppMobiService appMobiService;
	
	@Autowired
	private AppMobiUtil appMobiUtil;
	
	@GetMapping("/app-mobies")
	public Object list(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = false, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = false, defaultValue = "0") long toDate, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) String active) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		AppMobiFilter appMobiFilter=new AppMobiFilter();
		appMobiFilter.setFromDate(fromDate);
		appMobiFilter.setToDate(toDate);
		appMobiFilter.setKeySearch(keyword);
		appMobiFilter.setActive(active);
		appMobiFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=appMobiService.countAppMobiAll(appMobiFilter);
		List<AppMobi> appMobis=appMobiService.findAppMobiAll(appMobiFilter);
		List<Document> results=new ArrayList<Document>();
		for (AppMobi item : appMobis) {
			results.add(appMobiUtil.toAdminResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/app-mobies/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		AppMobi user=appMobiService.findAppMobiById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(appMobiUtil.toAdminResponse(user));
		return responseAPI.build();
	}
	
	@PutMapping("/app-mobies/update/active/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestParam(name = "active", required = true) boolean active){
		ResponseAPI responseAPI=new ResponseAPI();
		AppMobi user=appMobiService.setAppMobiActive(id, active);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(appMobiUtil.toAdminResponse(user));
		return responseAPI.build();
	}
	
	@DeleteMapping("/app-mobies/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		appMobiService.deleteAppMobiById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa thiết bị ứng dụng thành công");
		return responseAPI.build();
	}
}

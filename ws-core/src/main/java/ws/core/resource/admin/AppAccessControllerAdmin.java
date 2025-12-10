package ws.core.resource.admin;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ws.core.model.AppAccess;
import ws.core.model.filter.AppAccessFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.request.ReqAppAccessCreate;
import ws.core.model.request.ReqAppAccessUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.AppAccessUtil;
import ws.core.security.CustomUserDetails;
import ws.core.services.AppAccessService;

@RestController
@RequestMapping("/api/admin")
public class AppAccessControllerAdmin {
	
	@Autowired
	private AppAccessService appAccessService;
	
	@Autowired
	private AppAccessUtil appAccessUtil;
	
	@GetMapping("/app-accesses")
	public Object list(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = false, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = false, defaultValue = "0") long toDate, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) String active) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		AppAccessFilter appAccessFilter=new AppAccessFilter();
		appAccessFilter.setFromDate(fromDate);
		appAccessFilter.setToDate(toDate);
		appAccessFilter.setKeySearch(keyword);
		appAccessFilter.setActive(active);
		appAccessFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=appAccessService.countAppAccessAll(appAccessFilter);
		List<AppAccess> appAcceses=appAccessService.findAppAccessAll(appAccessFilter);
		List<Document> results=new ArrayList<Document>();
		for (AppAccess item : appAcceses) {
			results.add(appAccessUtil.toAdminResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/app-accesses/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		AppAccess appAccess=appAccessService.findAppAccessById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(appAccessUtil.toAdminResponse(appAccess));
		return responseAPI.build();
	}
	
	@PostMapping("/app-accesses")
	public Object create(@RequestBody @Valid ReqAppAccessCreate reqAppAccessCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		CustomUserDetails creator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		AppAccess appAccessCreate=appAccessService.createAppAccess(reqAppAccessCreate, creator.getUser());
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(appAccessUtil.toAdminResponse(appAccessCreate));
		return responseAPI.build();
	}
	
	@PutMapping("/app-accesses/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqAppAccessUpdate reqAppAccessUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		AppAccess appAccessCreate=appAccessService.updateAppAccess(id, reqAppAccessUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(appAccessUtil.toAdminResponse(appAccessCreate));
		return responseAPI.build();
	}
	
	@DeleteMapping("/app-accesses/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		appAccessService.deleteAppAccessById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa thành công");
		return responseAPI.build();
	}
}

package ws.core.resource.admin;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import ws.core.model.OrganizationCategory;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.filter.OrganizationCategoryFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.request.ReqOrganizationCategoryCreate;
import ws.core.model.request.ReqOrganizationCategoryUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.OrganizationCategoryUtil;
import ws.core.services.OrganizationCategoryService;

@RestController
@RequestMapping("/api/admin")
public class OrganizationCategoryControllerAdmin {
	
	@Autowired
	private OrganizationCategoryService organizationCategoryService;
	
	@Autowired
	private OrganizationCategoryUtil organizationCategoryUtil;
	
	@GetMapping("/organization-categories")
	public Object list(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		OrganizationCategoryFilter organizationCategoryFilter=new OrganizationCategoryFilter();
		organizationCategoryFilter.setKeySearch(keyword);
		organizationCategoryFilter.setActive(active);
		organizationCategoryFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));

		OrderByFilter orderByFilter=new OrderByFilter();
		orderByFilter.add("order", Direction.ASC);
		organizationCategoryFilter.setOrderByFilter(orderByFilter);
		
		long total=organizationCategoryService.countAll(organizationCategoryFilter);
		List<OrganizationCategory> leaderApproveTasks=organizationCategoryService.findAll(organizationCategoryFilter);
		List<Document> results=new ArrayList<Document>();
		for (OrganizationCategory item : leaderApproveTasks) {
			results.add(organizationCategoryUtil.toAdminResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/organization-categories/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		OrganizationCategory leaderApproveTask=organizationCategoryService.getById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationCategoryUtil.toAdminResponse(leaderApproveTask));
		return responseAPI.build();
	}
	
	@PostMapping("/organization-categories")
	public Object create(@RequestBody @Valid ReqOrganizationCategoryCreate reqOrganizationCategoryCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		OrganizationCategory appAccessCreate=organizationCategoryService.create(reqOrganizationCategoryCreate);
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationCategoryUtil.toAdminResponse(appAccessCreate));
		return responseAPI.build();
	}
	
	@PutMapping("/organization-categories/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqOrganizationCategoryUpdate reqOrganizationCategoryUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		OrganizationCategory leaderApproveTaskUpdate=organizationCategoryService.update(id, reqOrganizationCategoryUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationCategoryUtil.toAdminResponse(leaderApproveTaskUpdate));
		return responseAPI.build();
	}
	
	@DeleteMapping("/organization-categories/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		organizationCategoryService.deleteById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Xóa thành công");
		return responseAPI.build();
	}
}

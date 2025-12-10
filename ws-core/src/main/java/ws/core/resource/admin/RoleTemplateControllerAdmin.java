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
import ws.core.model.RoleTemplate;
import ws.core.model.filter.RoleTemplateFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.request.ReqRoleTemplateCreate;
import ws.core.model.request.ReqRoleTemplateUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.RoleTemplateUtil;
import ws.core.security.CustomUserDetails;
import ws.core.services.RoleTemplateService;

@RestController
@RequestMapping("/api/admin")
public class RoleTemplateControllerAdmin {
	
	@Autowired
	private RoleTemplateService roleTemplateService;
	
	@Autowired
	private RoleTemplateUtil roleTemplateUtil;
	
	@GetMapping("/role-template/list")
	public Object list(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) String active) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		RoleTemplateFilter roleTemplateFilter=new RoleTemplateFilter();
		roleTemplateFilter.setKeySearch(keyword);
		roleTemplateFilter.setActive(active);
		roleTemplateFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=roleTemplateService.countRoleTemplateAll(roleTemplateFilter);
		List<RoleTemplate> roleTemplates=roleTemplateService.findRoleTemplateAll(roleTemplateFilter);
		List<Document> results=new ArrayList<Document>();
		for (RoleTemplate item : roleTemplates) {
			results.add(roleTemplateUtil.toAdminResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/role-template/get/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		RoleTemplate roleTemplate=roleTemplateService.findRoleTemplateById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(roleTemplateUtil.toAdminResponse(roleTemplate));
		return responseAPI.build();
	}
	
	@PostMapping("/role-template/create")
	public Object create(@RequestBody @Valid ReqRoleTemplateCreate reqRoleTemplateCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		CustomUserDetails creator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RoleTemplate roleTemplateCreate=roleTemplateService.createRoleTemplate(reqRoleTemplateCreate, creator.getUser());
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(roleTemplateUtil.toAdminResponse(roleTemplateCreate));
		return responseAPI.build();
	}
	
	@PutMapping("/role-template/update/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqRoleTemplateUpdate reqRoleTemplateUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		RoleTemplate roleTemplateCreate=roleTemplateService.updateRoleTemplate(id, reqRoleTemplateUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(roleTemplateUtil.toAdminResponse(roleTemplateCreate));
		return responseAPI.build();
	}
	
	@DeleteMapping("/role-template/delete/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		roleTemplateService.deleteRoleTemplateById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa thành công");
		return responseAPI.build();
	}
}

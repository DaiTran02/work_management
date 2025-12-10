package ws.core.resource.site;

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
import ws.core.model.ClassifyTask;
import ws.core.model.filter.ClassifyTaskFilter;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.request.ReqClassifyTaskCreate;
import ws.core.model.request.ReqClassifyTaskUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.ClassifyTaskUtil;
import ws.core.security.CustomUserDetails;
import ws.core.services.ClassifyTaskService;

@RestController
@RequestMapping("/api/site")
public class ClassifyTaskControllerSite {
	
	@Autowired
	private ClassifyTaskService classifyTaskService;
	
	@Autowired
	private ClassifyTaskUtil classifyTaskUtil;
	
	@GetMapping("/classify-task")
	public Object list(
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		ClassifyTaskFilter classifyTaskFilter=new ClassifyTaskFilter();
		classifyTaskFilter.setOrganizationId(organizationId);
		classifyTaskFilter.setKeySearch(keyword);
		classifyTaskFilter.setActive(active);
		classifyTaskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));

		OrderByFilter orderByFilter=new OrderByFilter();
		orderByFilter.add("order", Direction.ASC);
		classifyTaskFilter.setOrderByFilter(orderByFilter);
		
		long total=classifyTaskService.countClassifyTaskAll(classifyTaskFilter);
		List<ClassifyTask> classifyTasks=classifyTaskService.findClassifyTaskAll(classifyTaskFilter);
		List<Document> results=new ArrayList<Document>();
		for (ClassifyTask item : classifyTasks) {
			results.add(classifyTaskUtil.toSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/classify-task/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		ClassifyTask classifyTask=classifyTaskService.findClassifyTaskById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(classifyTaskUtil.toSiteResponse(classifyTask));
		return responseAPI.build();
	}
	
	@PostMapping("/classify-task")
	public Object create(@RequestBody @Valid ReqClassifyTaskCreate reqClassifyTaskCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		CustomUserDetails creator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ClassifyTask appAccessCreate=classifyTaskService.createClassifyTask(reqClassifyTaskCreate, creator.getUser());
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(classifyTaskUtil.toSiteResponse(appAccessCreate));
		return responseAPI.build();
	}
	
	@PutMapping("/classify-task/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqClassifyTaskUpdate reqClassifyTaskUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		ClassifyTask classifyTaskUpdate=classifyTaskService.updateClassifyTask(id, reqClassifyTaskUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(classifyTaskUtil.toSiteResponse(classifyTaskUpdate));
		return responseAPI.build();
	}
	
	@DeleteMapping("/classify-task/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		classifyTaskService.deleteClassifyTaskById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Xóa thành công");
		return responseAPI.build();
	}
}

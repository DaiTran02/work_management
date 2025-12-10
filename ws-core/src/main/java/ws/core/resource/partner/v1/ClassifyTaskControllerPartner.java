package ws.core.resource.partner.v1;

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

import ws.core.model.ClassifyTask;
import ws.core.model.filter.ClassifyTaskFilter;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.ClassifyTaskUtil;
import ws.core.services.ClassifyTaskService;

@RestController
@RequestMapping("/api/partner/v1")
public class ClassifyTaskControllerPartner {
	
	@Autowired
	private ClassifyTaskService classifyTaskService;
	
	@Autowired
	private ClassifyTaskUtil classifyTaskUtil;
	
	@GetMapping("/classify-task")
	public Object list(
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		ClassifyTaskFilter classifyTaskFilter=new ClassifyTaskFilter();
		classifyTaskFilter.setOrganizationId(organizationId);
		classifyTaskFilter.setKeySearch(keyword);
		classifyTaskFilter.setActive(true);
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
	
}

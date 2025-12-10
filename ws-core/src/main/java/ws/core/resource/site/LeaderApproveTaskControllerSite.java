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
import ws.core.model.LeaderApproveTask;
import ws.core.model.filter.LeaderApproveTaskFilter;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.request.ReqLeaderApproveTaskCreate;
import ws.core.model.request.ReqLeaderApproveTaskUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.LeaderApproveTaskUtil;
import ws.core.security.CustomUserDetails;
import ws.core.services.LeaderApproveTaskService;

@RestController
@RequestMapping("/api/site")
public class LeaderApproveTaskControllerSite {
	
	@Autowired
	private LeaderApproveTaskService leaderApproveTaskService;
	
	@Autowired
	private LeaderApproveTaskUtil leaderApproveTaskUtil;
	
	@GetMapping("/leader-approve-task")
	public Object list(
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		LeaderApproveTaskFilter leaderApproveTaskFilter=new LeaderApproveTaskFilter();
		leaderApproveTaskFilter.setOrganizationId(organizationId);
		leaderApproveTaskFilter.setKeySearch(keyword);
		leaderApproveTaskFilter.setActive(active);
		leaderApproveTaskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));

		OrderByFilter orderByFilter=new OrderByFilter();
		orderByFilter.add("order", Direction.ASC);
		leaderApproveTaskFilter.setOrderByFilter(orderByFilter);
		
		long total=leaderApproveTaskService.countLeaderApproveTaskAll(leaderApproveTaskFilter);
		List<LeaderApproveTask> leaderApproveTasks=leaderApproveTaskService.findLeaderApproveTaskAll(leaderApproveTaskFilter);
		List<Document> results=new ArrayList<Document>();
		for (LeaderApproveTask item : leaderApproveTasks) {
			results.add(leaderApproveTaskUtil.toSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/leader-approve-task/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		LeaderApproveTask leaderApproveTask=leaderApproveTaskService.findLeaderApproveTaskById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(leaderApproveTaskUtil.toSiteResponse(leaderApproveTask));
		return responseAPI.build();
	}
	
	@PostMapping("/leader-approve-task")
	public Object create(@RequestBody @Valid ReqLeaderApproveTaskCreate reqLeaderApproveTaskCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		CustomUserDetails creator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		LeaderApproveTask appAccessCreate=leaderApproveTaskService.createLeaderApproveTask(reqLeaderApproveTaskCreate, creator.getUser());
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(leaderApproveTaskUtil.toSiteResponse(appAccessCreate));
		return responseAPI.build();
	}
	
	@PutMapping("/leader-approve-task/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqLeaderApproveTaskUpdate reqLeaderApproveTaskUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		LeaderApproveTask leaderApproveTaskUpdate=leaderApproveTaskService.updateLeaderApproveTask(id, reqLeaderApproveTaskUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(leaderApproveTaskUtil.toSiteResponse(leaderApproveTaskUpdate));
		return responseAPI.build();
	}
	
	@DeleteMapping("/leader-approve-task/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		leaderApproveTaskService.deleteLeaderApproveTaskById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Xóa thành công");
		return responseAPI.build();
	}
}

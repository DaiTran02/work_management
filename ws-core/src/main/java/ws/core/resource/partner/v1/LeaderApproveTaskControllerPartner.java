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

import ws.core.model.LeaderApproveTask;
import ws.core.model.filter.LeaderApproveTaskFilter;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.LeaderApproveTaskUtil;
import ws.core.services.LeaderApproveTaskService;

@RestController
@RequestMapping("/api/partner/v1")
public class LeaderApproveTaskControllerPartner {
	
	@Autowired
	private LeaderApproveTaskService leaderApproveTaskService;
	
	@Autowired
	private LeaderApproveTaskUtil leaderApproveTaskUtil;
	
	@GetMapping("/leader-approve-task")
	public Object list(
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		LeaderApproveTaskFilter leaderApproveTaskFilter=new LeaderApproveTaskFilter();
		leaderApproveTaskFilter.setOrganizationId(organizationId);
		leaderApproveTaskFilter.setKeySearch(keyword);
		leaderApproveTaskFilter.setActive(true);
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
}

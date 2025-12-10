package ws.core.resource.partner.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
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
import ws.core.advice.BadRequestExceptionAdvice;
import ws.core.enums.DataScopeType;
import ws.core.enums.TaskPriority;
import ws.core.enums.TaskState;
import ws.core.enums.TaskStatus;
import ws.core.model.Organization;
import ws.core.model.Task;
import ws.core.model.embeded.TaskDocInfo;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.filter.TaskFilter;
import ws.core.model.filter.embeded.TaskAssigneeFilter;
import ws.core.model.filter.embeded.TaskAssistantFilter;
import ws.core.model.filter.embeded.TaskFollowerFilter;
import ws.core.model.filter.embeded.TaskOwnerFilter;
import ws.core.model.filter.embeded.TaskSupportFilter;
import ws.core.model.request.ReqTaskChildCreate;
import ws.core.model.request.ReqTaskCreate;
import ws.core.model.request.ReqTaskDoAccept;
import ws.core.model.request.ReqTaskDoAssignUserAssignee;
import ws.core.model.request.ReqTaskDoAssignUserSupport;
import ws.core.model.request.ReqTaskDoComment;
import ws.core.model.request.ReqTaskDoComplete;
import ws.core.model.request.ReqTaskDoConfirm;
import ws.core.model.request.ReqTaskDoPending;
import ws.core.model.request.ReqTaskDoRating;
import ws.core.model.request.ReqTaskDoRedo;
import ws.core.model.request.ReqTaskDoRefuse;
import ws.core.model.request.ReqTaskDoRemind;
import ws.core.model.request.ReqTaskDoReport;
import ws.core.model.request.ReqTaskDoReverse;
import ws.core.model.request.ReqTaskDoUnAssignUserAssignee;
import ws.core.model.request.ReqTaskDoUnAssignUserSupport;
import ws.core.model.request.ReqTaskDoUnPending;
import ws.core.model.request.ReqTaskDoUpdateProcess;
import ws.core.model.request.ReqTaskUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.data.TaskCountByStatusModel;
import ws.core.model.response.util.TaskUtil;
import ws.core.services.OrganizationService;
import ws.core.services.TaskService;

@RestController
@RequestMapping("/api/partner/v1")
public class TaskControllerPartner {

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private TaskUtil taskUtil;
	
	@Autowired
	private OrganizationService organizationService;
	
	@GetMapping("/tasks/list-tasks-owner")
	public Object listTasksOwner(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = true) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "assistantOrganizationId", required = false) String assistantOrganizationId,
			@RequestParam(name = "assistantOrganizationGroupId", required = false) String assistantOrganizationGroupId,
			@RequestParam(name = "assistantOrganizationUserId", required = false) String assistantOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
		findOwnerFilter.setOrganizationId(ownerOrganizationId);
		if(ownerOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(ownerOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findOwnerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findOwnerFilter.getOrganizationIds().add(ownerOrganizationId);
			}
		}
		findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
		taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		if(assistantOrganizationId!=null || assistantOrganizationGroupId!=null || assistantOrganizationUserId!=null) {
			TaskAssistantFilter findAssistantFilter=new TaskAssistantFilter();
			findAssistantFilter.setOrganizationId(assistantOrganizationId);
			findAssistantFilter.setOrganizationGroupId(assistantOrganizationGroupId);
			findAssistantFilter.setOrganizationUserId(assistantOrganizationUserId);
			taskFilter.setFindAssistantFilters(Arrays.asList(findAssistantFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		taskFilter.setOrderByFilter(new OrderByFilter());
		taskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=taskService.countTaskAll(taskFilter);
		List<Task> tasks=taskService.findTaskAll(taskFilter);
		List<Document> results=new ArrayList<Document>();
		for (Task item : tasks) {
			results.add(taskUtil.toListSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/summary-tasks-owner")
	public Object summaryTasksOwner(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = true) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId,
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "assistantOrganizationId", required = false) String assistantOrganizationId,
			@RequestParam(name = "assistantOrganizationGroupId", required = false) String assistantOrganizationGroupId,
			@RequestParam(name = "assistantOrganizationUserId", required = false) String assistantOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
		findOwnerFilter.setOrganizationId(ownerOrganizationId);
		if(ownerOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(ownerOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findOwnerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findOwnerFilter.getOrganizationIds().add(ownerOrganizationId);
			}
		}
		findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
		taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		if(assistantOrganizationId!=null || assistantOrganizationGroupId!=null || assistantOrganizationUserId!=null) {
			TaskAssistantFilter findAssistantFilter=new TaskAssistantFilter();
			findAssistantFilter.setOrganizationId(assistantOrganizationId);
			findAssistantFilter.setOrganizationGroupId(assistantOrganizationGroupId);
			findAssistantFilter.setOrganizationUserId(assistantOrganizationUserId);
			taskFilter.setFindAssistantFilters(Arrays.asList(findAssistantFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		
		List<TaskCountByStatusModel> result=taskUtil.buildDataSummaryTasks(taskFilter);
		long total=taskUtil.getSumCount(result);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(result);
		return responseAPI.build();
	}

	@GetMapping("/tasks/achivement-tasks-owner")
	public Object achivementTasksOwner(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = true) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId,
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "assistantOrganizationId", required = false) String assistantOrganizationId,
			@RequestParam(name = "assistantOrganizationGroupId", required = false) String assistantOrganizationGroupId,
			@RequestParam(name = "assistantOrganizationUserId", required = false) String assistantOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
		findOwnerFilter.setOrganizationId(ownerOrganizationId);
		if(ownerOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(ownerOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findOwnerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findOwnerFilter.getOrganizationIds().add(ownerOrganizationId);
			}
		}
		findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
		taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		if(assistantOrganizationId!=null || assistantOrganizationGroupId!=null || assistantOrganizationUserId!=null) {
			TaskAssistantFilter findAssistantFilter=new TaskAssistantFilter();
			findAssistantFilter.setOrganizationId(assistantOrganizationId);
			findAssistantFilter.setOrganizationGroupId(assistantOrganizationGroupId);
			findAssistantFilter.setOrganizationUserId(assistantOrganizationUserId);
			taskFilter.setFindAssistantFilters(Arrays.asList(findAssistantFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		
		List<Document> result=taskUtil.buildDataAchivementTasks(taskFilter);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/count-tasks-owner")
	public Object countTasksOwner(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = true) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId,
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "assistantOrganizationId", required = false) String assistantOrganizationId,
			@RequestParam(name = "assistantOrganizationGroupId", required = false) String assistantOrganizationGroupId,
			@RequestParam(name = "assistantOrganizationUserId", required = false) String assistantOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
		findOwnerFilter.setOrganizationId(ownerOrganizationId);
		if(ownerOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(ownerOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findOwnerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findOwnerFilter.getOrganizationIds().add(ownerOrganizationId);
			}
		}
		findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
		taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		if(assistantOrganizationId!=null || assistantOrganizationGroupId!=null || assistantOrganizationUserId!=null) {
			TaskAssistantFilter findAssistantFilter=new TaskAssistantFilter();
			findAssistantFilter.setOrganizationId(assistantOrganizationId);
			findAssistantFilter.setOrganizationGroupId(assistantOrganizationGroupId);
			findAssistantFilter.setOrganizationUserId(assistantOrganizationUserId);
			taskFilter.setFindAssistantFilters(Arrays.asList(findAssistantFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		
		long total=taskService.countTaskAll(taskFilter);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/list-tasks-follower")
	public Object listTasksFollower(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = true) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "assistantOrganizationId", required = false) String assistantOrganizationId,
			@RequestParam(name = "assistantOrganizationGroupId", required = false) String assistantOrganizationGroupId,
			@RequestParam(name = "assistantOrganizationUserId", required = false) String assistantOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
		findFollowerFilter.setOrganizationId(followerOrganizationId);
		if(followerOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(followerOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findFollowerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findFollowerFilter.getOrganizationIds().add(followerOrganizationId);
			}
		}
		findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
		findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
		taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(assistantOrganizationId!=null || assistantOrganizationGroupId!=null || assistantOrganizationUserId!=null) {
			TaskAssistantFilter findAssistantFilter=new TaskAssistantFilter();
			findAssistantFilter.setOrganizationId(assistantOrganizationId);
			findAssistantFilter.setOrganizationGroupId(assistantOrganizationGroupId);
			findAssistantFilter.setOrganizationUserId(assistantOrganizationUserId);
			taskFilter.setFindAssistantFilters(Arrays.asList(findAssistantFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		taskFilter.setOrderByFilter(new OrderByFilter());
		taskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=taskService.countTaskAll(taskFilter);
		List<Task> tasks=taskService.findTaskAll(taskFilter);
		List<Document> results=new ArrayList<Document>();
		for (Task item : tasks) {
			results.add(taskUtil.toListSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/summary-tasks-follower")
	public Object summaryTasksFollower(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = true) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "assistantOrganizationId", required = false) String assistantOrganizationId,
			@RequestParam(name = "assistantOrganizationGroupId", required = false) String assistantOrganizationGroupId,
			@RequestParam(name = "assistantOrganizationUserId", required = false) String assistantOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
		findFollowerFilter.setOrganizationId(followerOrganizationId);
		if(followerOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(followerOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findFollowerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findFollowerFilter.getOrganizationIds().add(followerOrganizationId);
			}
		}
		findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
		findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
		taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(assistantOrganizationId!=null || assistantOrganizationGroupId!=null || assistantOrganizationUserId!=null) {
			TaskAssistantFilter findAssistantFilter=new TaskAssistantFilter();
			findAssistantFilter.setOrganizationId(assistantOrganizationId);
			findAssistantFilter.setOrganizationGroupId(assistantOrganizationGroupId);
			findAssistantFilter.setOrganizationUserId(assistantOrganizationUserId);
			taskFilter.setFindAssistantFilters(Arrays.asList(findAssistantFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		
		List<TaskCountByStatusModel> result=taskUtil.buildDataSummaryTasks(taskFilter);
		long total=taskUtil.getSumCount(result);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(result);
		return responseAPI.build();
	}

	@GetMapping("/tasks/achivement-tasks-follower")
	public Object achivementTasksFollower(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = true) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "assistantOrganizationId", required = false) String assistantOrganizationId,
			@RequestParam(name = "assistantOrganizationGroupId", required = false) String assistantOrganizationGroupId,
			@RequestParam(name = "assistantOrganizationUserId", required = false) String assistantOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
		findFollowerFilter.setOrganizationId(followerOrganizationId);
		if(followerOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(followerOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findFollowerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findFollowerFilter.getOrganizationIds().add(followerOrganizationId);
			}
		}
		findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
		findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
		taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(assistantOrganizationId!=null || assistantOrganizationGroupId!=null || assistantOrganizationUserId!=null) {
			TaskAssistantFilter findAssistantFilter=new TaskAssistantFilter();
			findAssistantFilter.setOrganizationId(assistantOrganizationId);
			findAssistantFilter.setOrganizationGroupId(assistantOrganizationGroupId);
			findAssistantFilter.setOrganizationUserId(assistantOrganizationUserId);
			taskFilter.setFindAssistantFilters(Arrays.asList(findAssistantFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		
		List<Document> result=taskUtil.buildDataAchivementTasks(taskFilter);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/count-tasks-follower")
	public Object countTasksFollower(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = true) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "assistantOrganizationId", required = false) String assistantOrganizationId,
			@RequestParam(name = "assistantOrganizationGroupId", required = false) String assistantOrganizationGroupId,
			@RequestParam(name = "assistantOrganizationUserId", required = false) String assistantOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
		findFollowerFilter.setOrganizationId(followerOrganizationId);
		if(followerOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(followerOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findFollowerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findFollowerFilter.getOrganizationIds().add(followerOrganizationId);
			}
		}
		findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
		findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
		taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(assistantOrganizationId!=null || assistantOrganizationGroupId!=null || assistantOrganizationUserId!=null) {
			TaskAssistantFilter findAssistantFilter=new TaskAssistantFilter();
			findAssistantFilter.setOrganizationId(assistantOrganizationId);
			findAssistantFilter.setOrganizationGroupId(assistantOrganizationGroupId);
			findAssistantFilter.setOrganizationUserId(assistantOrganizationUserId);
			taskFilter.setFindAssistantFilters(Arrays.asList(findAssistantFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		
		long total=taskService.countTaskAll(taskFilter);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/list-tasks-assignee")
	public Object listTasksAssignee(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = true) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
		findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
		if(assigneeOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(assigneeOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findAssigneeFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findAssigneeFilter.getOrganizationIds().add(assigneeOrganizationId);
			}
		}
		findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
		taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		taskFilter.setOrderByFilter(new OrderByFilter());
		taskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=taskService.countTaskAll(taskFilter);
		List<Task> tasks=taskService.findTaskAll(taskFilter);
		List<Document> results=new ArrayList<Document>();
		for (Task item : tasks) {
			results.add(taskUtil.toListSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/summary-tasks-assignee")
	public Object summaryTasksAssignee(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = true) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
		findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
		if(assigneeOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(assigneeOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findAssigneeFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findAssigneeFilter.getOrganizationIds().add(assigneeOrganizationId);
			}
		}
		findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
		taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		
		List<TaskCountByStatusModel> result=taskUtil.buildDataSummaryTasks(taskFilter);
		long total=taskUtil.getSumCount(result);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/achivement-tasks-assignee")
	public Object achivementTasksAssignee(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = true) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
		findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
		if(assigneeOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(assigneeOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findAssigneeFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findAssigneeFilter.getOrganizationIds().add(assigneeOrganizationId);
			}
		}
		findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
		taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		
		List<Document> result=taskUtil.buildDataAchivementTasks(taskFilter);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/count-tasks-assignee")
	public Object countTasksAssignee(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = true) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "hasAssignUserAssignee", required = false) Boolean hasAssignUserAssignee,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
		findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
		if(assigneeOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(assigneeOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findAssigneeFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findAssigneeFilter.getOrganizationIds().add(assigneeOrganizationId);
			}
		}
		findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
		taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserAssignee(hasAssignUserAssignee);
		
		long total=taskService.countTaskAll(taskFilter);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		return responseAPI.build();
	}

	@GetMapping("/tasks/list-tasks-support")
	public Object listTasksSupport(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "supportOrganizationId", required = true) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskSupportFilter findSupportFilter=new TaskSupportFilter();
		findSupportFilter.setOrganizationId(supportOrganizationId);
		if(supportOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(supportOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findSupportFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findSupportFilter.getOrganizationIds().add(supportOrganizationId);
			}
		}
		findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
		taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		taskFilter.setOrderByFilter(new OrderByFilter());
		taskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=taskService.countTaskAll(taskFilter);
		List<Task> tasks=taskService.findTaskAll(taskFilter);
		List<Document> results=new ArrayList<Document>();
		for (Task item : tasks) {
			results.add(taskUtil.toListSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/summary-tasks-support")
	public Object summaryTasksSupport(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "supportOrganizationId", required = true) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskSupportFilter findSupportFilter=new TaskSupportFilter();
		findSupportFilter.setOrganizationId(supportOrganizationId);
		if(supportOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(supportOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findSupportFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findSupportFilter.getOrganizationIds().add(supportOrganizationId);
			}
		}
		findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
		taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		
		List<TaskCountByStatusModel> result=taskUtil.buildDataSummaryTasks(taskFilter);
		long total=taskUtil.getSumCount(result);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(result);
		return responseAPI.build();
	}

	@GetMapping("/tasks/achivement-tasks-support")
	public Object achivementTasksSupport(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "supportOrganizationId", required = true) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskSupportFilter findSupportFilter=new TaskSupportFilter();
		findSupportFilter.setOrganizationId(supportOrganizationId);
		if(supportOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(supportOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findSupportFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findSupportFilter.getOrganizationIds().add(supportOrganizationId);
			}
		}
		findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
		taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		
		List<Document> result=taskUtil.buildDataAchivementTasks(taskFilter);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/count-tasks-support")
	public Object countTasksSupport(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "supportOrganizationId", required = true) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "hasAssignUserSupport", required = false) Boolean hasAssignUserSupport,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "docNumber", required = false) String docNumber,
			@RequestParam(name = "docSymbol", required = false) String docSymbol,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		TaskSupportFilter findSupportFilter=new TaskSupportFilter();
		findSupportFilter.setOrganizationId(supportOrganizationId);
		if(supportOrganizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(supportOrganizationId);
			
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			findSupportFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));
			
			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				findSupportFilter.getOrganizationIds().add(supportOrganizationId);
			}
		}
		findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
		taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, priority).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(docNumber!=null || docSymbol!=null) {
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(docNumber);
			taskDocInfo.setSymbol(docSymbol);
			taskFilter.setDocInfo(taskDocInfo);
		}
		taskFilter.setHasAssignUserSupport(hasAssignUserSupport);
		
		long total=taskService.countTaskAll(taskFilter);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/list-tasks-delivered-by-doc")
	public Object listTasksDeliveredByDoc(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "docId", required = true) String docId, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.getDocIds().add(docId);
		taskFilter.setTaskRoot(true);
		
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, status).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		taskFilter.setOrderByFilter(new OrderByFilter());
		taskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=taskService.countTaskAll(taskFilter);
		List<Task> tasks=taskService.findTaskAll(taskFilter);
		List<Document> results=new ArrayList<Document>();
		for (Task item : tasks) {
			results.add(taskUtil.toListSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/summary-tasks-delivered-by-doc")
	public Object summaryTasksDeliveredByDoc(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "docId", required = true) String docId, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId,
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Tìm theo dữ liệu nhiệm vụ */
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.getDocIds().add(docId);
		taskFilter.setTaskRoot(true);
		
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		
		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}
		
		if(assigneeOrganizationId!=null || assigneeOrganizationUserId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(assigneeOrganizationUserId);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}
		
		if(supportOrganizationId!=null || supportOrganizationUserId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(supportOrganizationUserId);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}
		
		if(followerOrganizationId!=null || followerOrganizationGroupId!=null || followerOrganizationUserId!=null) {
			TaskFollowerFilter findFollowerFilter=new TaskFollowerFilter();
			findFollowerFilter.setOrganizationId(followerOrganizationId);
			findFollowerFilter.setOrganizationGroupId(followerOrganizationGroupId);
			findFollowerFilter.setOrganizationUserId(followerOrganizationUserId);
			taskFilter.setFindFollowerFilters(Arrays.asList(findFollowerFilter));
		}
		
		taskFilter.setKeySearch(keyword);
		if(priority!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskPriority.class, priority)) {
				taskFilter.setPriority(EnumUtils.getEnumIgnoreCase(TaskPriority.class, status).getKey());
			}else {
				throw new BadRequestExceptionAdvice("priority [" + priority + "] không hợp lệ");
			}
		}
		
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskStatus.class, status)) {
				taskFilter.setStatus(EnumUtils.getEnumIgnoreCase(TaskStatus.class, status));
			}else {
				throw new BadRequestExceptionAdvice("status [" + status + "] không hợp lệ");
			}
		}
		
		List<TaskCountByStatusModel> result=taskUtil.buildDataSummaryTasks(taskFilter);
		long total=taskUtil.getSumCount(result);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(result);
		return responseAPI.build();
	}

	@GetMapping("/tasks/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task task=taskService.getTaskById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(task));
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/{parentTaskId}/sub-tasks")
	public Object getSubTasks(@PathVariable(name = "parentTaskId", required = true) String parentTaskId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setParentIds(Arrays.asList(parentTaskId));
		
		List<Task> tasks=taskService.findTaskAll(taskFilter);
		List<Document> results=new ArrayList<Document>();
		for (Task item : tasks) {
			results.add(taskUtil.toListSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(results.size());
		responseAPI.setResult(results);
		return responseAPI.build();
	}

	@PostMapping("/tasks")
	public Object create(@RequestBody @Valid ReqTaskCreate reqTaskCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskCreate=taskService.createTask(reqTaskCreate);
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskCreate));
		return responseAPI.build();
	}
	
	@PostMapping("/tasks/{id}")
	public Object createChild(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskChildCreate reqTaskChildCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskCreate=taskService.createTaskChild(reqTaskChildCreate, id);
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskCreate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskUpdate reqTaskUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.updateTask(id, reqTaskUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@DeleteMapping("/tasks/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		taskService.deleteTaskById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa thành công");
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/state")
	public Object getState() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(TaskState.values());
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/status")
	public Object getStatus() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(TaskStatus.values());
		return responseAPI.build();
	}
	
	@GetMapping("/tasks/priority")
	public Object getPriority() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(TaskPriority.values());
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-refuse")
	public Object doRefuse(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskDoRefuse reqTaskDoRefuse){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doRefuseTask(id, reqTaskDoRefuse);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-accept")
	public Object doAccept(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskDoAccept reqTaskDoAccept){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doAcceptTask(id, reqTaskDoAccept);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-completed")
	public Object doCompleted(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskDoComplete reqTaskDoComplete){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doCompleteTask(id, reqTaskDoComplete);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-reverse")
	public Object doReverse(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskDoReverse reqTaskDoReverse){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doReverseTask(id, reqTaskDoReverse);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-report")
	public Object doReport(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskDoReport reqTaskDoReport){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doReportTask(id, reqTaskDoReport);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-confirm")
	public Object doConfirm(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskDoConfirm reqTaskDoConfirm){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doConfirmTask(id, reqTaskDoConfirm);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-pending")
	public Object doPending(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskDoPending reqTaskDoPending){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doPendingTask(id, reqTaskDoPending);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-unpending")
	public Object doUnPending(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskDoUnPending reqTaskDoUnPending){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doUnPendingTask(id, reqTaskDoUnPending);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-rating")
	public Object doRating(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskDoRating reqTaskDoRating){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doRatingTask(id, reqTaskDoRating);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-redo")
	public Object doRedo(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqTaskDoRedo reqTaskDoRedo){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doRedoTask(id, reqTaskDoRedo);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-update-process")
	public Object doUpdateProcess(
			@PathVariable(name = "id", required = true) String taskId,
			@RequestBody @Valid ReqTaskDoUpdateProcess reqTaskDoUpdateProcess){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doUpdateProcessTask(taskId, reqTaskDoUpdateProcess);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-remind")
	public Object doRemind(
			@PathVariable(name = "id", required = true) String taskId,
			@RequestBody @Valid ReqTaskDoRemind reqTaskDoRemind){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doRemindTask(taskId, reqTaskDoRemind);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-comment")
	public Object doComment(
			@PathVariable(name = "id", required = true) String taskId,
			@RequestBody @Valid ReqTaskDoComment reqTaskDoComment){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doCommentTask(taskId, reqTaskDoComment);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-reply-comment/{parentCommentId}")
	public Object doComment(
			@PathVariable(name = "id", required = true) String taskId,
			@PathVariable(name = "parentCommentId", required = true) String parentCommentId,
			@RequestBody @Valid ReqTaskDoComment reqTaskDoComment){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doReplyCommentTask(taskId, parentCommentId, reqTaskDoComment);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-assign-user-assignee/{organizationId}")
	public Object doSetUserAssignee(
			@PathVariable(name = "id", required = true) String taskId,
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@RequestBody @Valid ReqTaskDoAssignUserAssignee reqTaskDoAssignUserAssignee){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doAssignUserAssignee(taskId, organizationId, reqTaskDoAssignUserAssignee);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-unassign-user-assignee/{organizationId}")
	public Object doUnsetUserAssignee(
			@PathVariable(name = "id", required = true) String taskId,
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@RequestBody @Valid ReqTaskDoUnAssignUserAssignee reqTaskDoUnAssignUserAssignee){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doUnAssignUserAssignee(taskId, organizationId, reqTaskDoUnAssignUserAssignee);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-assign-user-support/{organizationId}")
	public Object doSetUserSupport(
			@PathVariable(name = "id", required = true) String taskId,
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@RequestBody @Valid ReqTaskDoAssignUserSupport reqTaskDoAssignUserSupport){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doAssignUserSupport(taskId, organizationId, reqTaskDoAssignUserSupport);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
	@PutMapping("/tasks/{id}/do-unassign-user-support/{organizationId}")
	public Object doUnsetUserSupport(
			@PathVariable(name = "id", required = true) String taskId,
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@RequestBody @Valid ReqTaskDoUnAssignUserSupport reqTaskDoUnAssignUserSupport){
		ResponseAPI responseAPI=new ResponseAPI();
		
		Task taskUpdate=taskService.doUnAssignUserSupport(taskId, organizationId, reqTaskDoUnAssignUserSupport);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(taskUtil.toDetailSiteResponse(taskUpdate));
		return responseAPI.build();
	}
	
}

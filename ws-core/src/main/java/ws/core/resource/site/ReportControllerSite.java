package ws.core.resource.site;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.advice.BadRequestExceptionAdvice;
import ws.core.enums.DataScopeType;
import ws.core.enums.DocCategory;
import ws.core.enums.DocStatus;
import ws.core.enums.TaskPriority;
import ws.core.enums.TaskSource;
import ws.core.enums.TaskStatus;
import ws.core.model.Doc;
import ws.core.model.Organization;
import ws.core.model.ReportKpi;
import ws.core.model.Task;
import ws.core.model.filter.DocFilter;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.filter.TaskFilter;
import ws.core.model.filter.embeded.DocOwnerFilter;
import ws.core.model.filter.embeded.TaskAssigneeFilter;
import ws.core.model.filter.embeded.TaskAssistantFilter;
import ws.core.model.filter.embeded.TaskFollowerFilter;
import ws.core.model.filter.embeded.TaskOwnerFilter;
import ws.core.model.filter.embeded.TaskSupportFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.DocUtil;
import ws.core.model.response.util.TaskUtil;
import ws.core.services.DocService;
import ws.core.services.OrganizationService;
import ws.core.services.TaskService;
import ws.core.util.DateTimeUtil;

@RestController
@RequestMapping("/api/site")
public class ReportControllerSite {

	@Autowired
	private DocService docService;

	@Autowired
	private DocUtil docUtil;

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskUtil taskUtil;

	@Autowired
	private OrganizationService organizationService;

	@GetMapping("/report/list-docs")
	public Object listDocs(
			@RequestParam(name = "skip", required = false, defaultValue = "0") int skip, 
			@RequestParam(name = "limit", required = false, defaultValue = "0") int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "organizationGroupId", required = false) String organizationGroupId,
			@RequestParam(name = "organizationUserId", required = false) String organizationUserId, 
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType, 
			@RequestParam(name = "category", required = false) String category, 
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "classifyTaskId", required = false) String classifyTaskId,
			@RequestParam(name = "leaderApproveTaskId", required = false) String leaderApproveTaskId) {
		ResponseAPI responseAPI=new ResponseAPI();

		DocFilter docFilter=new DocFilter();
		docFilter.setFromRegDate(fromDate);
		docFilter.setToRegDate(toDate);

		DocOwnerFilter docOwnerFilter=new DocOwnerFilter();
		docOwnerFilter.setOrganizationId(organizationId);
		if(organizationId!=null && dataScopeType!=null) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(organizationId);

			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			docOwnerFilter.setOrganizationIds(organizations.stream().map(e->e.getId()).collect(Collectors.toList()));

			if(dataScopeType.equals(DataScopeType.incChildOrgs)) {
				docOwnerFilter.getOrganizationIds().add(organizationId);
			}
		}
		docOwnerFilter.setOrganizationGroupId(organizationGroupId);
		docOwnerFilter.setOrganizationUserId(organizationUserId);
		docFilter.setOwnerFilter(docOwnerFilter);

		docFilter.setCategory(EnumUtils.getEnumIgnoreCase(DocCategory.class, category));
		docFilter.setStatus(EnumUtils.getEnumIgnoreCase(DocStatus.class, status));
		docFilter.setClassifyTaskId(classifyTaskId);
		docFilter.setLeaderApproveTaskId(leaderApproveTaskId);
		docFilter.setActive(true);
		docFilter.setTrash(false);
		docFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));

		long total=docService.countDocAll(docFilter);
		List<Doc> docs=docService.findDocAll(docFilter);
		List<Document> results=new ArrayList<Document>();
		results=docs.stream().map(e->docUtil.toSiteResponse(e)).toList();

		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}

	@GetMapping("/report/list-tasks-owner")
	public Object listTasksOwner(
			@RequestParam(name = "skip", required = false, defaultValue = "0") int skip, 
			@RequestParam(name = "limit", required = false, defaultValue = "0") int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType,
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId, 
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId, 
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "assistantOrganizationId", required = false) String assistantOrganizationId,
			@RequestParam(name = "assistantOrganizationGroupId", required = false) String assistantOrganizationGroupId,
			@RequestParam(name = "assistantOrganizationUserId", required = false) String assistantOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "source", required = false) String source,
			@RequestParam(name = "docCategory", required = false) String docCategory, 
			@RequestParam(name = "classifyTaskId", required = false) String classifyTaskId,
			@RequestParam(name = "leaderApproveTaskId", required = false) String leaderApproveTaskId) {
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

		if(assigneeOrganizationId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(null);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}

		if(supportOrganizationId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(null);
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

		if(source!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskSource.class, source)) {
				taskFilter.setTaskSource(EnumUtils.getEnumIgnoreCase(TaskSource.class, source));
			}else {
				throw new BadRequestExceptionAdvice("source [" + source + "] không hợp lệ");
			}
		}

		/* Tìm theo dữ liệu văn bản */
		if(classifyTaskId!=null || leaderApproveTaskId!=null || docCategory!=null) {
			DocFilter docFilter=new DocFilter();
			docFilter.setClassifyTaskId(classifyTaskId);
			docFilter.setLeaderApproveTaskId(leaderApproveTaskId);
			if(docCategory!=null) {
				docFilter.setCategory(EnumUtils.getEnumIgnoreCase(DocCategory.class, docCategory));
			}
			List<Doc> docs=docService.findDocAll(docFilter);
			taskFilter.setDocIds(docs.stream().map(e->e.getId()).toList());
		}
		taskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));

		long total = taskService.countTaskAll(taskFilter);
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

	@GetMapping("/report/list-tasks-follower")
	public Object listTasksFollower(
			@RequestParam(name = "skip", required = false, defaultValue = "0") int skip, 
			@RequestParam(name = "limit", required = false, defaultValue = "0") int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId, 
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId, 
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority, 
			@RequestParam(name = "source", required = false) String source,
			@RequestParam(name = "docCategory", required = false) String docCategory, 
			@RequestParam(name = "classifyTaskId", required = false) String classifyTaskId,
			@RequestParam(name = "leaderApproveTaskId", required = false) String leaderApproveTaskId,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType) {
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

		if(assigneeOrganizationId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(null);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}

		if(supportOrganizationId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(null);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}

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

		if(source!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskSource.class, source)) {
				taskFilter.setTaskSource(EnumUtils.getEnumIgnoreCase(TaskSource.class, source));
			}else {
				throw new BadRequestExceptionAdvice("source [" + source + "] không hợp lệ");
			}
		}

		/* Tìm theo dữ liệu văn bản */
		if(classifyTaskId!=null || leaderApproveTaskId!=null || docCategory!=null) {
			DocFilter docFilter=new DocFilter();
			docFilter.setClassifyTaskId(classifyTaskId);
			docFilter.setLeaderApproveTaskId(leaderApproveTaskId);
			if(docCategory!=null) {
				docFilter.setCategory(EnumUtils.getEnumIgnoreCase(DocCategory.class, docCategory));
			}
			List<Doc> docs=docService.findDocAll(docFilter);
			taskFilter.setDocIds(docs.stream().map(e->e.getId()).toList());
		}
		taskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));

		long total = taskService.countTaskAll(taskFilter);
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

	@GetMapping("/report/list-tasks-assignee")
	public Object listTasksAssignee(
			@RequestParam(name = "skip", required = false, defaultValue = "0") int skip, 
			@RequestParam(name = "limit", required = false, defaultValue = "0") int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId, 
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId, 
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority,
			@RequestParam(name = "source", required = false) String source,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType) {
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
			TaskOwnerFilter findOwner=new TaskOwnerFilter();
			findOwner.setOrganizationId(ownerOrganizationId);
			findOwner.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwner));
		}

		if(supportOrganizationId!=null) {
			TaskSupportFilter findSupportFilter=new TaskSupportFilter();
			findSupportFilter.setOrganizationId(supportOrganizationId);
			findSupportFilter.setOrganizationUserId(null);
			taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));
		}

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

		if(source!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskSource.class, source)) {
				taskFilter.setTaskSource(EnumUtils.getEnumIgnoreCase(TaskSource.class, source));
			}else {
				throw new BadRequestExceptionAdvice("source [" + source + "] không hợp lệ");
			}
		}

		/* Tìm theo dữ liệu văn bản */
		/*DocFilter docFilter=new DocFilter();
		docFilter.setFromRegDate(fromDate);
		docFilter.setToRegDate(toDate);
		List<Doc> docs=docService.findDocAll(docFilter);
		taskFilter.setDocIds(docs.stream().map(e->e.getId()).toList());
		 */
		taskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));

		long total = taskService.countTaskAll(taskFilter);
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

	@GetMapping("/report/list-tasks-support")
	public Object listTasksSupport(
			@RequestParam(name = "skip", required = false, defaultValue = "0") int skip, 
			@RequestParam(name = "limit", required = false, defaultValue = "0") int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId, 
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId, 
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId, 
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "status", required = false) String status, 
			@RequestParam(name = "priority", required = false) String priority,
			@RequestParam(name = "source", required = false) String source,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType) {
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
		findSupportFilter.setOrganizationUserId( supportOrganizationUserId);
		taskFilter.setFindSupportFilters(Arrays.asList(findSupportFilter));

		if(ownerOrganizationId!=null || ownerOrganizationUserId!=null) {
			TaskOwnerFilter findOwnerFilter=new TaskOwnerFilter();
			findOwnerFilter.setOrganizationId(ownerOrganizationId);
			findOwnerFilter.setOrganizationUserId(ownerOrganizationUserId);
			taskFilter.setFindOwnerFilters(Arrays.asList(findOwnerFilter));
		}

		if(assigneeOrganizationId!=null) {
			TaskAssigneeFilter findAssigneeFilter=new TaskAssigneeFilter();
			findAssigneeFilter.setOrganizationId(assigneeOrganizationId);
			findAssigneeFilter.setOrganizationUserId(null);
			taskFilter.setFindAssigneeFilters(Arrays.asList(findAssigneeFilter));
		}

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

		if(source!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(TaskSource.class, source)) {
				taskFilter.setTaskSource(EnumUtils.getEnumIgnoreCase(TaskSource.class, source));
			}else {
				throw new BadRequestExceptionAdvice("source [" + source + "] không hợp lệ");
			}
		}

		/* Tìm theo dữ liệu văn bản */
		/*DocFilter docFilter=new DocFilter();
		docFilter.setFromRegDate(fromDate);
		docFilter.setToRegDate(toDate);
		List<Doc> docs=docService.findDocAll(docFilter);
		taskFilter.setDocIds(docs.stream().map(e->e.getId()).toList());
		 */
		taskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));

		long total = taskService.countTaskAll(taskFilter);
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

	@GetMapping("/report/list-tasks-kpi")
	public Object listTaskKpi(
			@RequestParam(name = "skip", required = false, defaultValue = "0") int skip, 
			@RequestParam(name = "limit", required = false, defaultValue = "0") int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate,
			@RequestParam(name = "isKpi", required = true) Boolean isKpi,
			@RequestParam(name = "ownerOrganizationId", required = false) String ownerOrganizationId, 
			@RequestParam(name = "ownerOrganizationUserId", required = false) String ownerOrganizationUserId,
			@RequestParam(name = "supportOrganizationId", required = false) String supportOrganizationId,
			@RequestParam(name = "supportOrganizationUserId", required = false) String supportOrganizationUserId,
			@RequestParam(name = "assigneeOrganizationId", required = false) String assigneeOrganizationId, 
			@RequestParam(name = "assigneeOrganizationUserId", required = false) String assigneeOrganizationUserId,
			@RequestParam(name = "followerOrganizationId", required = false) String followerOrganizationId,
			@RequestParam(name = "followerOrganizationGroupId", required = false) String followerOrganizationGroupId,
			@RequestParam(name = "followerOrganizationUserId", required = false) String followerOrganizationUserId,
			@RequestParam(name = "dataScopeType", required = false) DataScopeType dataScopeType) {
		ResponseAPI responseAPI = new ResponseAPI();
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFromDate(fromDate);
		taskFilter.setToDate(toDate);
		taskFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		taskFilter.setKpi(isKpi);

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
			findOwnerFilter.setOnlyOwner(null);
			
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

		List<Task> tasks=taskService.findTaskAll(taskFilter);
		List<Document> results=new ArrayList<Document>();

		int taskCompleted = 0;
		int taskNotCompleted = 0;
		int taskCompletedButThroughExpired = 0;
		int taskCompletedButNotThroughExpired = 0;
		int taskIsRatedHigherThanThreeStars = 0;

		for(Task item : tasks) {
			if(item.getEndTime() != null) {
				if(DateTimeUtil.isDateBetweenTwoDate(item.getEndTime(), new Date(fromDate), new Date(toDate))) {
					if(item.getCompleted() != null) {
						taskCompleted++;
						if(item.getCompleted().getCompletedStatus().equals("tronghan")) {
							taskCompletedButNotThroughExpired++;
						}else if(item.getCompleted().getCompletedStatus().equals("quahan")) {
							taskCompletedButThroughExpired++;
						}
						if(item.getRating() != null) {
							if(item.getRating().getStar() > 3) {
								taskIsRatedHigherThanThreeStars++;
							}
						}
					}else {
						taskNotCompleted++;
					}
					results.add(taskUtil.toListSiteResponse(item));
				}
			}
		}

		int totalTaskCompleted = taskCompletedButThroughExpired + taskCompletedButNotThroughExpired;

		int markA = taskCompleted == 0 ? 0 : (taskCompleted * 100) / (taskNotCompleted + taskCompleted);
		int markB = taskCompletedButNotThroughExpired == 0 ? 0 : (taskCompletedButNotThroughExpired * 100) / totalTaskCompleted;
		int markC = taskIsRatedHigherThanThreeStars == 0 ? 0 : taskIsRatedHigherThanThreeStars * 100 / totalTaskCompleted;
		int totalPercent = taskIsRatedHigherThanThreeStars == 0 ? (markA + markB) /2 : (markA + markB + markC) / 3;
		int totalMark = totalPercent == 0 ? 0 : (totalPercent * 70) / 100 + 30;

		ReportKpi reportKpi = new ReportKpi();
		reportKpi.setMarkA(markA);
		reportKpi.setMarkB(markB);
		reportKpi.setMarkC(markC);
		reportKpi.setTotalMark(totalMark);
		reportKpi.setTotalPercent(totalPercent);
		reportKpi.setTaskCompleted(taskCompleted);
		reportKpi.setTaskNotCompleted(taskNotCompleted);
		reportKpi.setTaskCompletedButNotThroughExpired(taskCompletedButNotThroughExpired);
		reportKpi.setTaskCompletedButThroughExpired(taskCompletedButThroughExpired);
		reportKpi.setTaskIsRatedHigherThanThreeStars(taskIsRatedHigherThanThreeStars);
		reportKpi.setListTasks(results);


		responseAPI.setOk();
		responseAPI.setTotal(tasks.size());
		responseAPI.setResult(reportKpi);
		return responseAPI.build();
	}

	@GetMapping("/report/data-scope-type")
	public Object getDataScopeType() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(DataScopeType.values());
		return responseAPI.build();
	}
}

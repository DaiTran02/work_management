package ws.core.resource.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.EnumUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.advice.BadRequestExceptionAdvice;
import ws.core.enums.UserAccessStatus;
import ws.core.model.Organization;
import ws.core.model.User;
import ws.core.model.filter.LogAccessFilter;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.filter.UserFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.OrganizationUtil;
import ws.core.model.response.util.UserUtil;
import ws.core.services.LogAccessService;
import ws.core.services.OrganizationService;
import ws.core.services.UserService;

@RestController
@RequestMapping("/api/admin")
public class ReportControllerAdmin {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserUtil userUtil;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrganizationUtil organizationUtil;
	
	@Autowired
	private LogAccessService logAccessService;
	
	@GetMapping("/report/list-organizations")
	public Object getOrganizationAll(
			@RequestParam(name = "parentId", required = false) String parentId,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(name = "organizationCategoryId", required = false) String organizationCategoryId,
			@RequestParam(name = "hasContainUsers", required = false) Boolean hasContainUsers,
			@RequestParam(name = "includeUserId", required = false) String includeUserId,
			@RequestParam(name = "level", required = false) String level
			) throws InterruptedException, ExecutionException {
		ResponseAPI responseAPI=new ResponseAPI();
		List<Document> results=new ArrayList<Document>();
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		if(parentId!=null) {
			organizationFilter.setParentId(parentId);
		}else {
			organizationFilter.setRoot(true);
		}
		organizationFilter.setKeySearch(keyword);
		organizationFilter.setActive(active);
		organizationFilter.setOrganizationCategoryId(organizationCategoryId);
		organizationFilter.setHasContainUsers(hasContainUsers);
		organizationFilter.setIncludeUserId(includeUserId);
		organizationFilter.setLevel(level);
		
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		ExecutorService executor = Executors.newFixedThreadPool((organizations.size()>0)?organizations.size():1);
		List<Future<Document>> listFuture = new ArrayList<Future<Document>>();
		
		for(Organization organization:organizations) {
			listFuture.add(executor.submit(new Callable<Document>() {
				@Override
				public Document call() throws Exception {
					return organizationUtil.buildMetaOrganizations(organization);
				}
			}));
		}
		
		for (Future<Document> future : listFuture) {
			results.add(future.get());
		}
		executor.shutdown();
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/report/list-users-system")
	public Object getUsersSystemList(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "used", required = false) Boolean used,
			@RequestParam(name = "active", required = false) Boolean active) throws InterruptedException, ExecutionException {
		ResponseAPI responseAPI=new ResponseAPI();

		UserFilter userFilter=new UserFilter();
		userFilter.setHasUsed(used);
		userFilter.setActive(active);
		userFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=userService.countUserAll(userFilter);
		List<User> users=userService.findUserAll(userFilter);
		
		List<Document> results=new ArrayList<Document>();
		for (User item : users) {
			results.add(convertUserSystem(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/report/list-users-using")
	public Object getUserUsingList(
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "includeSub", required = false, defaultValue = "false") boolean includeSub,
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "status", required = false) String status) throws InterruptedException, ExecutionException {
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization = organizationService.getOrganizationById(organizationId);
		
		UserAccessStatus accessStatus=null;
		if(status!=null) {
			if(EnumUtils.isValidEnumIgnoreCase(UserAccessStatus.class, status)==false) {
				throw new BadRequestExceptionAdvice("Yêu cầu không phù hợp");
			}
			accessStatus=EnumUtils.getEnum(UserAccessStatus.class, status);
		}
		
		LogAccessFilter logAccessFilter=new LogAccessFilter();
		logAccessFilter.setFromDate(fromDate);
		logAccessFilter.setToDate(toDate);
		List<String> userIdsLoged = logAccessService.getDistinctUsers(logAccessFilter);

		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.toReportUserUsings(userIdsLoged, organization, includeSub, accessStatus));
		return responseAPI.build();
	}
	
	protected Document convertUserSystem(User user){
		return userUtil.toAdminResponse(user);
	}
}

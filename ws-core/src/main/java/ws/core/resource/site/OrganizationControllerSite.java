package ws.core.resource.site;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ws.core.model.Organization;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.OrganizationUtil;
import ws.core.services.OrganizationService;

@RestController
@RequestMapping("/api/site")
public class OrganizationControllerSite {
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrganizationUtil organizationUtil;
	
	/**
	 * Hàm đệ quy lấy đơn vị hiện tại và đơn vị con, có tới cấp mấy
	 * @param organization
	 * @param childLevel
	 * @param currentLevel
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	protected Document buildMetaOrganizations(Organization organization, int childLevel, int currentLevel, boolean onlyOrganizationInfo) throws InterruptedException, ExecutionException{
		Document result=new Document();
		/* Đơn vị hiện tại */
		if(onlyOrganizationInfo) {
			result=organizationUtil.toSiteResponseOnlyOrganization(organization);
		}else {
			result=organizationUtil.toSiteResponse(organization);
		}
		
		/* Tìm đơn vị con */
		List<Document> subOrganizations=new ArrayList<Document>();
		if(childLevel>0 && currentLevel<=childLevel) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(organization.getId());
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			if(organizations.size()>0) {
				ExecutorService executor = Executors.newFixedThreadPool(organizations.size());
				List<Future<Document>> listFuture = new ArrayList<Future<Document>>();
				
				for (Organization item : organizations) {
					listFuture.add(executor.submit(new Callable<Document>() {
						@Override
						public Document call() throws Exception {
							return buildMetaOrganizations(item, childLevel, currentLevel+1, onlyOrganizationInfo);
						}
					}));	
				}
				
				for (Future<Document> future : listFuture) {
					subOrganizations.add(future.get());
				}
				executor.shutdown();
			}
		}
		
		/* Công thêm kết quả đơn vị con */
		result.append("subOrganizations", subOrganizations);
		return result;
	}
	
	/**
	 * Hàm đệ quy lấy đơn vị hiện tại và đơn vị con, có tới cấp mấy và đơn vị con có trong organizations (tìm được theo từ khóa)
	 * @param organization
	 * @param childLevel
	 * @param currentLevel
	 * @param withOrganizations
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	protected Document buildMetaOrganizations(Organization organization, int childLevel, int currentLevel, List<Organization> withOrganizations, boolean onlyOrganizationInfo) throws InterruptedException, ExecutionException{
		Document result=new Document();
		result=organizationUtil.toSiteResponse(organization);
		
		List<Document> subOrganizations=new ArrayList<Document>();
		if(childLevel>0 && currentLevel<=childLevel) {
			OrganizationFilter organizationFilter=new OrganizationFilter();
			organizationFilter.setParentId(organization.getId());
			List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
			if(organizations.size()>0) {
				ExecutorService executor = Executors.newFixedThreadPool(organizations.size());
				List<Future<Document>> listFuture = new ArrayList<Future<Document>>();
				
				for (Organization item : organizations) {
					if(withOrganizations!=null && withOrganizations.size()>0) {
						Organization check=null;
						for(Organization withOrganization:withOrganizations) {
							if(withOrganization.getPath().contains(item.getId())) {
								check=withOrganization;
								break;
							}
						}
						
						if(check!=null) {
							if(check.getId().equals(item.getId())) {
								listFuture.add(executor.submit(new Callable<Document>() {
									@Override
									public Document call() throws Exception {
										return buildMetaOrganizations(item, childLevel, currentLevel+1, onlyOrganizationInfo);
									}
								}));
							}else {
								listFuture.add(executor.submit(new Callable<Document>() {
									@Override
									public Document call() throws Exception {
										return buildMetaOrganizations(item, childLevel, currentLevel+1, withOrganizations, onlyOrganizationInfo);
									}
								}));
							}
						}
					}else {
						listFuture.add(executor.submit(new Callable<Document>() {
							@Override
							public Document call() throws Exception {
								return buildMetaOrganizations(item, childLevel, currentLevel+1, onlyOrganizationInfo);
							}
						}));	
					}
				}
				
				for (Future<Document> future : listFuture) {
					subOrganizations.add(future.get());
				}
				executor.shutdown();
			}
		}
		result.append("subOrganizations", subOrganizations);
		return result;
	}
	
	@GetMapping("/organizations")
	public Object getListOrganizations(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "parentId", required = false) String parentId,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false, defaultValue = "true") Boolean active,
			@RequestParam(name = "organizationCategoryId", required = false) String organizationCategoryId) throws InterruptedException, ExecutionException {
		ResponseAPI responseAPI=new ResponseAPI();
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		if(parentId!=null) {
			organizationFilter.setParentId(parentId);
		}else {
			organizationFilter.setRoot(true);
		}
		organizationFilter.setKeySearch(keyword);
		organizationFilter.setActive(active);
		organizationFilter.setOrganizationCategoryId(organizationCategoryId);
		organizationFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=organizationService.countOrganizationAll(organizationFilter);
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		
		List<Document> results=new ArrayList<Document>();
		for (Organization item : organizations) {
			results.add(organizationUtil.toSiteResponse(item));
		}
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/trees")
	public Object getListOrganizationsTree(
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "childLevel", required = false, defaultValue = "0") int childLevel) throws InterruptedException, ExecutionException {
		ResponseAPI responseAPI=new ResponseAPI();
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		if(keyword==null) {
			organizationFilter.setRoot(true);
		}else {
			organizationFilter.setKeySearch(keyword);
		}
		organizationFilter.setActive(true);
		
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		
		List<Document> results=new ArrayList<Document>();
		if(keyword==null) {
			for (Organization item : organizations) {
				results.add(buildMetaOrganizations(item, childLevel, 1, false));
			}
		}else {
			List<String> rootIds=new ArrayList<String>();
			organizations.stream().forEach(e->{
				if(e.getPath().contains("/")) {
					String rootId=e.getPath().split("/")[0];
					boolean exists=false;
					for(String id:rootIds) {
						if(id.equals(rootId)) {
							exists=true;
						}
					}
					if(exists==false) {
						rootIds.add(rootId);
					}
				}
			});
			
			organizationFilter=new OrganizationFilter();
			organizationFilter.setIds(rootIds);
			
			organizations=organizationService.findOrganizationAll(organizationFilter);
			for (Organization item : organizations) {
				results.add(buildMetaOrganizations(item, childLevel, 1, organizations, false));
			}
		}
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/trees-root")
	public Object getListOrganizationsTreeRoot() throws InterruptedException, ExecutionException {
		ResponseAPI responseAPI=new ResponseAPI();
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setRoot(true);
		organizationFilter.setActive(true);
		
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		List<Document> results=new ArrayList<Document>();
		for (Organization item : organizations) {
			results.add(buildMetaOrganizations(item, 100, 1, true));
		}
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}")
	public Object getOrganization(@PathVariable(name = "organizationId", required = true) String organizationId) {
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization = null;
		if(ObjectId.isValid(organizationId)) {
			organization=organizationService.getOrganizationById(organizationId);
		}else {
			organization=organizationService.getOrganizationByUnitCode(organizationId);
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.toAdminResponse(organization));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/list-sub-organizations")
	public Object getListSubOrganizations(@PathVariable(name = "organizationId", required = true) String organizationId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setActive(true);
		organizationFilter.setParentId(organizationId);
		
		long total=organizationService.countOrganizationAll(organizationFilter);
		List<Organization> organizations=organizationService.findOrganizationAll(organizationFilter);
		
		List<Document> results=new ArrayList<Document>();
		for (Organization item : organizations) {
			results.add(organizationUtil.toAdminResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	
	/*-------------- Users -------------------*/
	@GetMapping("/organizations/{organizationId}/list-users")
	public Object getListUsers(
			@PathVariable(name = "organizationId", required = true) String organizationId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationById(organizationId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUserOrganizationExpandToResponse(organization, true));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/get-user/{userId}")
	public Object getUserDetails(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "userId", required = true) String userId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationById(organizationId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getUserOrganizationExpandToResponse(organization, userId, true));
		return responseAPI.build();
	}
	
	
	/*------------- Groups --------------*/
	@GetMapping("/organizations/{organizationId}/list-groups")
	public Object getListGroupsInOrganization(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "keyword", required = false) String keyword){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationById(organizationId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.searchListGroupInOrganizationToResponse(organization, keyword));
		return responseAPI.build();
	}

	@GetMapping("/organizations/{organizationId}/get-group/{groupId}")
	public Object getGroupDetails(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "groupId", required = true) String groupId) {
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.findOrganizationGroupById(organizationId, groupId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getGroupInOrganizationToResponse(organization, groupId));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/list-users-in-group/{groupId}")
	public Object getListUsersInGroupOrganization(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "groupId", required = true) String groupId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.findOrganizationGroupById(organizationId, groupId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUsersInGroupOrganizationToResponse(organization, groupId));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/list-users-not-in-all-group")
	public Object getListUsersNotInGroupOrganization(
			@PathVariable(name = "organizationId", required = true) String organizationId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationById(organizationId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUsersNotInAllGroupOrganizationToResponse(organization));
		return responseAPI.build();
	}

	@GetMapping("/organizations/{organizationId}/list-users-for-owner-task")
	public Object getListUsersForOwnerTask(
			@PathVariable(name = "organizationId", required = true) String organizationId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationById(organizationId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUserOrganizationExpandToResponse(organization, true));
		return responseAPI.build();
	}
	
	
	/*------------- Roles --------------*/
	@GetMapping("/organizations/{organizationId}/list-roles")
	public Object getListRolesInOrganization(
			@PathVariable(name = "organizationId", required = true) String organizationId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationById(organizationId);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListRoleInOrganizationToResponse(organization));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/get-role/{roleId}")
	public Object getRoleDetails(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "roleId", required = true) String roleId) {
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.findOrganizationRoleById(organizationId, roleId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getRoleInOrganizationToResponse(organization, roleId));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/list-users-in-role/{roleId}")
	public Object getListUsersInRoleOrganization(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "roleId", required = true) String roleId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.findOrganizationRoleById(organizationId, roleId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUsersInRoleOrganizationToResponse(organization, roleId));
		return responseAPI.build();
	}
}

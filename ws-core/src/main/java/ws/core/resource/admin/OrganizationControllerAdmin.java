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
import ws.core.enums.OrganizationLevel;
import ws.core.model.Organization;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.request.ReqGroupOrganizationCreate;
import ws.core.model.request.ReqGroupOrganizationUpdate;
import ws.core.model.request.ReqOrganizationCreate;
import ws.core.model.request.ReqOrganizationUpdate;
import ws.core.model.request.ReqRoleOrganizationCreate;
import ws.core.model.request.ReqRoleOrganizationUpdate;
import ws.core.model.request.ReqUserOrganizationAdds;
import ws.core.model.request.ReqUserOrganizationRemoves;
import ws.core.model.request.ReqUserOrganizationUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.OrganizationUtil;
import ws.core.security.CustomUserDetails;
import ws.core.services.OrganizationService;

@RestController
@RequestMapping("/api/admin")
public class OrganizationControllerAdmin {
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrganizationUtil organizationUtil;
	
	@PostMapping("/organizations")
	public Object create(@RequestBody @Valid ReqOrganizationCreate reqOrganizationCreate){
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails creator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Organization organizationCreate=organizationService.createOrganization(reqOrganizationCreate, creator.getUser());
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.toAdminResponse(organizationCreate));
		return responseAPI.build();
	}
	
	@PutMapping("/organizations/{organizationId}")
	public Object update(@PathVariable(name = "organizationId", required = true) String organizationId,
			@RequestBody @Valid ReqOrganizationUpdate reqOrganizationUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.updateOrganization(organizationId, reqOrganizationUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.toAdminResponse(organization));
		return responseAPI.build();
	}
	
	@DeleteMapping("/organizations/{organizationId}")
	public Object delete(@PathVariable(name = "organizationId", required = true) String organizationId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		organizationService.deleteOrganizationById(organizationId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa tổ chức thành công");
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}")
	public Object get(@PathVariable(name = "organizationId", required = true) String organizationId) {
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationById(organizationId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.toAdminResponse(organization));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations")
	public Object list(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "parentId", required = false) String parentId,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(name = "organizationCategoryId", required = false) String organizationCategoryId,
			@RequestParam(name = "hasContainUsers", required = false) Boolean hasContainUsers,
			@RequestParam(name = "includeUserId", required = false) String includeUserId,
			@RequestParam(name = "level", required = false) String level) {
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
		organizationFilter.setHasContainUsers(hasContainUsers);
		organizationFilter.setIncludeUserId(includeUserId);
		organizationFilter.setLevel(level);
		organizationFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
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
	
	@GetMapping("/organizations/get-level")
	public Object getLevel() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(OrganizationLevel.values());
		return responseAPI.build();
	}
	
	@PostMapping("/organizations/{organizationId}/add-users")
	public Object addUsers(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@RequestBody @Valid ReqUserOrganizationAdds reqUserOrganizationAdds){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.addUsersToOrganization(organizationId, reqUserOrganizationAdds);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUserOrganizationExpandToResponse(organization, true));
		return responseAPI.build();
	}
	
	@DeleteMapping("/organizations/{organizationId}/remove-users")
	public Object removeUsers(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@RequestBody @Valid ReqUserOrganizationRemoves reqUserOrganizationRemoves){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.removeUsersToOrganization(organizationId, reqUserOrganizationRemoves);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUserOrganizationExpandToResponse(organization, true));
		return responseAPI.build();
	}
	
	@PutMapping("/organizations/{organizationId}/update-user/{userId}")
	public Object updateUser(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "userId", required = true) String userId,
			@RequestBody @Valid ReqUserOrganizationUpdate reqUserOrganizationUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.updateUserToOrganization(organizationId, userId, reqUserOrganizationUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getUserOrganizationExpandToResponse(organization, userId, true));
		return responseAPI.build();
	}
	
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
	public Object getUser(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "userId", required = true) String userId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationById(organizationId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getUserOrganizationExpandToResponse(organization, userId, true));
		return responseAPI.build();
	}
	
	
	
	
	@PostMapping("/organizations/{organizationId}/create-group")
	public Object createGroup(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@RequestBody @Valid ReqGroupOrganizationCreate reqGroupOrganizationCreate){
		CustomUserDetails userCreator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.createGroupToOrganization(organizationId, reqGroupOrganizationCreate, userCreator.getUser());
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListGroupInOrganizationToResponse(organization));
		return responseAPI.build();
	}
	
	@PutMapping("/organizations/{organizationId}/update-group/{groupId}")
	public Object updateGroup(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "groupId", required = true) String groupId,
			@RequestBody @Valid ReqGroupOrganizationUpdate reqGroupOrganizationUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.updateGroupFromOrganization(organizationId, groupId, reqGroupOrganizationUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getGroupInOrganizationToResponse(organization, groupId));
		return responseAPI.build();
	}
	
	@DeleteMapping("/organizations/{organizationId}/delete-group/{groupId}")
	public Object removeGroup(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "groupId", required = true) String groupId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.deleteGroupFromOrganization(organizationId, groupId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListGroupInOrganizationToResponse(organization));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/get-group/{groupId}")
	public Object getGroup(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "groupId", required = true) String groupId) {
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.findOrganizationGroupById(organizationId, groupId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getGroupInOrganizationToResponse(organization, groupId));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/list-groups")
	public Object getListGroups(
			@PathVariable(name = "organizationId", required = true) String organizationId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationById(organizationId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListGroupInOrganizationToResponse(organization));
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
	
	
	
	
	
	
	
	
	
	/**
	 * Thêm vai trò cho đơn vị
	 * @param organizationId
	 * @param reqRoleOrganizationCreate
	 * @return
	 */
	@PostMapping("/organizations/{organizationId}/create-role")
	public Object addRole(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@RequestBody @Valid ReqRoleOrganizationCreate reqRoleOrganizationCreate){
		CustomUserDetails userCreator = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.createRoleOrganization(organizationId, reqRoleOrganizationCreate, userCreator.getUser());
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListRoleInOrganizationToResponse(organization));
		return responseAPI.build();
	}
	
	@PutMapping("/organizations/{organizationId}/update-role/{roleId}")
	public Object updateRole(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "roleId", required = true) String roleId,
			@RequestBody @Valid ReqRoleOrganizationUpdate reqRoleOrganizationUpdate){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.updateRoleOrganization(organizationId, roleId, reqRoleOrganizationUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getRoleInOrganizationToResponse(organization, roleId));
		return responseAPI.build();
	}
	
	@DeleteMapping("/organizations/{organizationId}/delete-role/{roleId}")
	public Object removeRole(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "roleId", required = true) String roleId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.deleteRoleOrganization(organizationId, roleId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListRoleInOrganizationToResponse(organization));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/get-role/{roleId}")
	public Object getRole(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "roleId", required = true) String roleId) {
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.findOrganizationRoleById(organizationId, roleId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getRoleInOrganizationToResponse(organization, roleId));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/list-roles")
	public Object getListRoles(
			@PathVariable(name = "organizationId", required = true) String organizationId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationById(organizationId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListRoleInOrganizationToResponse(organization));
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
	
	@GetMapping("/organizations/{organizationId}/list-users-not-in-role/{roleId}")
	public Object getListUsersNotInRoleOrganization(
			@PathVariable(name = "organizationId", required = true) String organizationId,
			@PathVariable(name = "roleId", required = true) String roleId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.findOrganizationRoleById(organizationId, roleId);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUsersNotInRoleInOrganizationToResponse(organization, roleId));
		return responseAPI.build();
	}
	
}

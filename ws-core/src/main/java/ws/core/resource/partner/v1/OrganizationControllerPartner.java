package ws.core.resource.partner.v1;

import java.util.ArrayList;
import java.util.List;

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
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.Organization;
import ws.core.model.filter.OrganizationFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.request.ReqOrganizationCreatePartner;
import ws.core.model.request.ReqOrganizationUpdatePartner;
import ws.core.model.request.ReqUserOrganizationAddsPartner;
import ws.core.model.request.ReqUserOrganizationRemovesPartner;
import ws.core.model.response.ResUserOrganizationAddsPartner;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.OrganizationUtil;
import ws.core.services.OrganizationService;

@RestController
@RequestMapping("/api/partner/v1")
public class OrganizationControllerPartner {
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrganizationUtil organizationUtil;
	
	@PostMapping("/organizations")
	public Object create(@RequestBody @Valid ReqOrganizationCreatePartner reqOrganizationCreatePartner){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organizationCreate=organizationService.createOrganization(reqOrganizationCreatePartner, null);
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.toPartnerResponse(organizationCreate));
		return responseAPI.build();
	}
	
	@PutMapping("/organizations/{unitCode}")
	public Object update(@PathVariable(name = "unitCode", required = true) String unitCode,
			@RequestBody @Valid ReqOrganizationUpdatePartner reqOrganizationUpdatePartner){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.updateOrganization(unitCode, reqOrganizationUpdatePartner);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.toAdminResponse(organization));
		return responseAPI.build();
	}
	
	@DeleteMapping("/organizations/{unitCode}")
	public Object delete(@PathVariable(name = "unitCode", required = true) String unitCode) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		organizationService.deleteOrganizationByUnitCode(unitCode);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa tổ chức thành công");
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/get-by-unitcode/{unitCode}")
	public Object getOrganizationByUnitCode(
			@PathVariable(name = "unitCode", required = true) String unitCode
			) {
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationByUnitCode(unitCode);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.toPartnerResponse(organization));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{unitCode}")
	public Object get(@PathVariable(name = "unitCode", required = true) String unitCode) {
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationByUnitCode(unitCode);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.toPartnerResponse(organization));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations")
	public Object list(
			@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "parentId", required = false) String parentId,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(name = "organizationCategoryId", required = false) String organizationCategoryId) {
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
			results.add(organizationUtil.toPartnerResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{unitCode}/list-users")
	public Object getListUsers(
			@PathVariable(name = "unitCode", required = true) String unitCode){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationByUnitCode(unitCode);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUserOrganizationExpandToResponsePartner(organization, false));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{unitCode}/get-user/{userId}")
	public Object getUser(
			@PathVariable(name = "unitCode", required = true) String unitCode,
			@PathVariable(name = "userId", required = true) String userId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationByUnitCode(unitCode);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getUserOrganizationExpandToResponsePartner(organization, userId, false));
		return responseAPI.build();
	}
	
	@PostMapping("/organizations/{unitCode}/add-users")
	public Object addUsers(
			@PathVariable(name = "unitCode", required = true) String unitCode,
			@RequestBody @Valid ReqUserOrganizationAddsPartner reqUserOrganizationAddsPartner){
		ResponseAPI responseAPI=new ResponseAPI();
		
		/* Kết quả thêm vào đơn vị, bao nhiêu thành công và bao nhiêu thất bại */
		ResUserOrganizationAddsPartner resUserOrganizationAddsPartner = organizationService.addUsersToOrganizationPartner(unitCode, reqUserOrganizationAddsPartner);
		Document result = new Document();
		result.append("successes", resUserOrganizationAddsPartner.getSuccesses());
		result.append("failess", resUserOrganizationAddsPartner.getFailes());
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(result);
		return responseAPI.build();
	}
	
	@DeleteMapping("/organizations/{unitCode}/remove-users")
	public Object removeUsers(
			@PathVariable(name = "unitCode", required = true) String unitCode,
			@RequestBody @Valid ReqUserOrganizationRemovesPartner reqUserOrganizationRemovesPartner){
		ResponseAPI responseAPI=new ResponseAPI();
		organizationService.removeUsersToOrganizationPartner(unitCode, reqUserOrganizationRemovesPartner);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/get-group/{groupId}")
	public Object getGroup(
			@PathVariable(name = "unitCode", required = true) String unitCode,
			@PathVariable(name = "groupId", required = true) String groupId) {
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationByUnitCode(unitCode);
		if(!organization.getGroupOrganizationExpand(groupId).isPresent()) {
			throw new NotFoundElementExceptionAdvice("Không tồn tại ["+groupId+"]");
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getGroupInOrganizationToResponse(organization, groupId));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{organizationId}/list-groups")
	public Object getListGroups(
			@PathVariable(name = "unitCode", required = true) String unitCode,
			@RequestParam(name = "keyword", required = false) String keyword){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationByUnitCode(unitCode);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.searchListGroupInOrganizationToResponse(organization, keyword));
		return responseAPI.build();
	}

	@GetMapping("/organizations/{unitCode}/list-users-in-group/{groupId}")
	public Object getListUsersInGroupOrganization(
			@PathVariable(name = "organizationId", required = true) String unitCode,
			@PathVariable(name = "groupId", required = true) String groupId){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationByUnitCode(unitCode);
		if(!organization.getGroupOrganizationExpand(groupId).isPresent()) {
			throw new NotFoundElementExceptionAdvice("Không tồn tại ["+groupId+"]");
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUsersInGroupOrganizationToResponse(organization, groupId));
		return responseAPI.build();
	}
	
	@GetMapping("/organizations/{unitCode}/list-users-not-in-all-group")
	public Object getListUsersNotInGroupOrganization(
			@PathVariable(name = "unitCode", required = true) String unitCode){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationByUnitCode(unitCode);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUsersNotInAllGroupOrganizationToResponse(organization));
		return responseAPI.build();
	}

	@GetMapping("/organizations/{unitCode}/list-users-for-owner-task")
	public Object getListUsersForOwnerTask(
			@PathVariable(name = "unitCode", required = true) String unitCode){
		ResponseAPI responseAPI=new ResponseAPI();
		Organization organization=organizationService.getOrganizationByUnitCode(unitCode);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(organizationUtil.getListUserOrganizationExpandToResponse(organization, false));
		return responseAPI.build();
	}
	
}

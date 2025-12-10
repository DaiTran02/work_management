package ws.core.resource.site;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import ws.core.enums.EventCalendarType;
import ws.core.model.EventResource;
import ws.core.model.filter.CreatorFilter;
import ws.core.model.filter.EventResourceFilter;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.request.ReqEventResourceCreate;
import ws.core.model.request.ReqEventResourceUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.EventResourceUtil;
import ws.core.security.CustomUserDetails;
import ws.core.services.EventResourceService;
import ws.core.services.PermissionService;

@RestController
@RequestMapping("/website")
public class EventResourceControllerSite {

	@Autowired
	private EventResourceService eventResourceService;
	
	@Autowired
	private EventResourceUtil eventResourceUtil;
	
	@Autowired
	private PermissionService permissionService;
	
	@GetMapping("/event-resource")
	public Object getList(
			@RequestParam(name = "skip", required = false, defaultValue = "0") int skip, 
			@RequestParam(name = "limit", required = false, defaultValue = "0") int limit, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "userId", required = false) String userId,
			@RequestParam(name = "group", required = false) Integer group,
			@RequestParam(name = "keyword", required = false) String keyword) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		EventResourceFilter eventCalendarFilter=new EventResourceFilter();
		eventCalendarFilter.setGroup(group);
		eventCalendarFilter.setKeyword(keyword);
		eventCalendarFilter.setTrash(false);
		if(userId!=null) {
			eventCalendarFilter.setType(EventCalendarType.personal.getKey());
		}else {
			eventCalendarFilter.setType(EventCalendarType.organization.getKey());
		}
		
		if(organizationId!=null) {
			CreatorFilter creatorFilter=new CreatorFilter();
			creatorFilter.setOrganizationId(organizationId);
			eventCalendarFilter.setCreatorFilter(creatorFilter);
		}
		eventCalendarFilter.setOrderByFilter(new OrderByFilter());
		eventCalendarFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=eventResourceService.countAll(eventCalendarFilter);
		List<EventResource> eventResources=eventResourceService.findAll(eventCalendarFilter);
		List<Document> results=new ArrayList<Document>();
		for (EventResource item : eventResources) {
			results.add(eventResourceUtil.toSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/event-resource/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		EventResourceFilter eventResourceFilter=new EventResourceFilter();
		eventResourceFilter.setId(id);
		eventResourceFilter.setTrash(false);
		
		Optional<EventResource> findEventCalendar=eventResourceService.findOne(eventResourceFilter);
		if(findEventCalendar.isPresent()) {
			EventResource eventResource = findEventCalendar.get();
			responseAPI.setStatus(HttpStatus.OK);
			responseAPI.setMessage("Thành công");
			responseAPI.setResult(eventResourceUtil.toSiteResponse(eventResource));
			return responseAPI.build();
		}
		responseAPI.setStatus(HttpStatus.NOT_FOUND);
		responseAPI.setMessage("Không tồn tại");
		return responseAPI.build();
	}
	
	@PostMapping("/event-resource")
	public Object create(@RequestBody @Valid ReqEventResourceCreate reqEventResourceCreate) {
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(reqEventResourceCreate.getType().equals(EventCalendarType.personal.getKey())  || (reqEventResourceCreate.getType().equals(EventCalendarType.organization.getKey()) && hasPermission(reqEventResourceCreate.getCreator().getOrganizationId(), userRequest.getUser().getId(), "quanlylichcongtac"))) {
			EventResource eventResource=eventResourceService.create(reqEventResourceCreate, userRequest.getUser());
			
			responseAPI.setStatus(HttpStatus.OK);
			responseAPI.setResult(eventResourceUtil.toSiteResponse(eventResource));
			return responseAPI.build();
		}
		responseAPI.setStatus(HttpStatus.NOT_ACCEPTABLE);
		responseAPI.setMessage("Không được phép thêm, vì không có quyền");
		return responseAPI.build();
	}
	
	@PutMapping("/event-resource/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqEventResourceUpdate reqEventResourceUpdate) {
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		EventResource eventResource = eventResourceService.getById(id);
		if(eventResource.canUpdate() && (eventResource.getType().equals(EventCalendarType.personal.getKey()) && eventResource.getCreator().getOrganizationUserId().equals(userRequest.getUser().getId()))
				|| (eventResource.getType().equals(EventCalendarType.organization.getKey()) && hasPermission(eventResource.getCreator().getOrganizationId(), userRequest.getUser().getId(), "quanlylichcongtac"))) {
			
			eventResource=eventResourceService.update(id, reqEventResourceUpdate, userRequest.getUser());
			
			responseAPI.setStatus(HttpStatus.OK);
			responseAPI.setResult(eventResourceUtil.toSiteResponse(eventResource));
			return responseAPI.build();
		}
		responseAPI.setStatus(HttpStatus.NOT_ACCEPTABLE);
		responseAPI.setMessage("Không được phép cập nhật, vì không có quyền");
		return responseAPI.build();
	}
	
	@DeleteMapping("/event-resource/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		EventResourceFilter eventResourceFilter=new EventResourceFilter();
		eventResourceFilter.setId(id);
		eventResourceFilter.setTrash(false);
		
		EventResource eventResource=eventResourceService.getOne(eventResourceFilter);
		if((eventResource.getType().equals(EventCalendarType.personal.getKey()) && eventResource.getCreator().getOrganizationUserId().equals(userRequest.getUser().getId()))
				|| (eventResource.getType().equals(EventCalendarType.organization.getKey()) && hasPermission(eventResource.getCreator().getOrganizationId(), userRequest.getUser().getId(), "quanlylichcongtac"))) {
			eventResource=eventResourceService.delete(id, userRequest.getUser());
			responseAPI.setStatus(HttpStatus.OK);
			responseAPI.setMessage("Xóa thành công");
			return responseAPI.build();
		}else {
			responseAPI.setStatus(HttpStatus.NOT_ACCEPTABLE);
			responseAPI.setMessage("Không được phép xóa, vì không có quyền");
			return responseAPI.build();
		}
	}
	
	private boolean hasPermission(String organizationId, String userId, String permissonKey) {
		return permissionService.hasPermission(organizationId, userId, permissonKey);
	}
}

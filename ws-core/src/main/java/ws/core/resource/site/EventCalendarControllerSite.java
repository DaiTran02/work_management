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
import ws.core.enums.EventCalendarPeriod;
import ws.core.enums.EventCalendarType;
import ws.core.enums.EventCalendarUserStatus;
import ws.core.model.EventCalendar;
import ws.core.model.filter.CreatorFilter;
import ws.core.model.filter.EventCalendarFilter;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.request.ReqEventCalendarCreate;
import ws.core.model.request.ReqEventCalendarUpdate;
import ws.core.model.request.ReqEventCalendarUserDoConfirmed;
import ws.core.model.request.ReqEventCalendarUserDoDelegacy;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.EventCalendarUtil;
import ws.core.security.CustomUserDetails;
import ws.core.services.EventCalendarService;

@RestController
@RequestMapping("/website")
public class EventCalendarControllerSite {
	@Autowired
	private EventCalendarService eventCalendarService;

	@Autowired
	private EventCalendarUtil eventCalendarUtil;
	
	@GetMapping("/event-calendar")
	public Object getList(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "userId", required = false) String userId,
			@RequestParam(name = "excludeCreator", required = false) Boolean excludeCreator,
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(name = "separateDay", required = false, defaultValue = "true") Boolean separateDay) {
		ResponseAPI responseAPI=new ResponseAPI();
		EventCalendarFilter eventCalendarFilter=new EventCalendarFilter();
		eventCalendarFilter.setFromDate(fromDate);
		eventCalendarFilter.setToDate(toDate);
		
		CreatorFilter creatorFilter=new CreatorFilter();
		creatorFilter.setOrganizationId(organizationId);
		creatorFilter.setOrganizationUserId(userId);
		eventCalendarFilter.setCreatorFilter(creatorFilter);
		
		if(userId!=null) {
			eventCalendarFilter.setType(EventCalendarType.personal.getKey());
		}else {
			eventCalendarFilter.setType(EventCalendarType.organization.getKey());
		}
		eventCalendarFilter.setStatus(status);
		eventCalendarFilter.setExcludeCreator(excludeCreator);
		eventCalendarFilter.setKeyword(keyword);
		eventCalendarFilter.setTrash(false);
		
		OrderByFilter orderByFilter=new OrderByFilter();
		orderByFilter.add("from", Direction.DESC);
		eventCalendarFilter.setOrderByFilter(orderByFilter);
		
		long total=eventCalendarService.countAll(eventCalendarFilter);
		List<EventCalendar> eventCalendars=eventCalendarService.findAll(eventCalendarFilter);
		List<Document> results=new ArrayList<Document>();
		for (EventCalendar item : eventCalendars) {
			/* Nếu có yêu cầu tách ngày */
			if(separateDay!=null && separateDay.booleanValue() && item.isCanSeparate()) {
				List<EventCalendar> splitEventToDays=eventCalendarUtil.splitEventToDays(item, fromDate, toDate);
				for (EventCalendar splitEventToDay : splitEventToDays) {
					results.add(eventCalendarUtil.toSiteResponse(splitEventToDay));
				}
			}else {
				results.add(eventCalendarUtil.toSiteResponse(item));
			}
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/event-calendar/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		EventCalendarFilter eventCalendarFilter=new EventCalendarFilter();
		eventCalendarFilter.setId(id);
		eventCalendarFilter.setTrash(false);
		
		EventCalendar eventCalendar=eventCalendarService.getOne(eventCalendarFilter);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(eventCalendarUtil.toSiteResponse(eventCalendar));
		return responseAPI.build();
	}
	
	@PostMapping("/event-calendar")
	public Object create(@RequestBody @Valid ReqEventCalendarCreate reqEventCalendarCreate) {
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		EventCalendar eventCalendar = eventCalendarService.create(reqEventCalendarCreate, userRequest.getUser());
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thêm mới thành công");
		responseAPI.setResult(eventCalendarUtil.toSiteResponse(eventCalendar));
		return responseAPI.build();
	}
	
	@PutMapping("/event-calendar/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqEventCalendarUpdate reqEventCalendarUpdate) {
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		EventCalendar eventCalendar = eventCalendarService.update(id, reqEventCalendarUpdate, userRequest.getUser());
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thêm mới thành công");
		responseAPI.setResult(eventCalendarUtil.toSiteResponse(eventCalendar));
		return responseAPI.build();
	}
	
	@DeleteMapping("/event-calendar/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		eventCalendarService.deleteById(id, userRequest.getUser());
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Xóa thành công");
		return responseAPI.build();
	}
	
	@GetMapping("/event-calendar/get-colors")
	public Object getColors() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(new ArrayList<>(eventCalendarUtil.getColors().keySet()));
		return responseAPI.build();
	}
	
	@GetMapping("/event-calendar/get-confirm-status")
	public Object getConfirmStatus() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(EventCalendarUserStatus.values());
		return responseAPI.build();
	}
	
	@GetMapping("/event-calendar/get-types")
	public Object getTypes() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(EventCalendarType.values());
		return responseAPI.build();
	}
	
	@GetMapping("/event-calendar/get-period")
	public Object getPeriod() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(EventCalendarPeriod.values());
		return responseAPI.build();
	}
	
	@PutMapping("/event-calendar/{id}/user-do-confirmed")
	public Object userDoConfirmed(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqEventCalendarUserDoConfirmed reqEventCalendarUserDoConfirmed) {
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		EventCalendar eventCalendar = eventCalendarService.userDoConfirm(id, reqEventCalendarUserDoConfirmed, userRequest.getUser());
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(eventCalendarUtil.toSiteResponse(eventCalendar));
		return responseAPI.build();
	}
	
	@PutMapping("/event-calendar/{id}/user-do-delegacy")
	public Object userDoDelegacy(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqEventCalendarUserDoDelegacy reqEventCalendarUserDoDelegacy) {
		ResponseAPI responseAPI=new ResponseAPI();
		CustomUserDetails userRequest = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		EventCalendar eventCalendar = eventCalendarService.userDoDelegacy(id, reqEventCalendarUserDoDelegacy, userRequest.getUser());
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(eventCalendarUtil.toSiteResponse(eventCalendar));
		return responseAPI.build();
	}
}

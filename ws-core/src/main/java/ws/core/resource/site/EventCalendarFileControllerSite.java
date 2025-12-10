package ws.core.resource.site;

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
import ws.core.model.EventCalendarFile;
import ws.core.model.filter.CreatorFilter;
import ws.core.model.filter.EventCalendarFileFilter;
import ws.core.model.filter.OrderByFilter;
import ws.core.model.filter.OrderByFilter.Direction;
import ws.core.model.request.ReqEventCalendarFileCreate;
import ws.core.model.request.ReqEventCalendarFileUpdate;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.EventCalendarFileUtil;
import ws.core.services.EventCalendarFileService;

@RestController
@RequestMapping("/website")
public class EventCalendarFileControllerSite {
	@Autowired
	protected EventCalendarFileService eventCalendarFileService;

	@Autowired
	private EventCalendarFileUtil eventCalendarFileUtil;
	
	@GetMapping("/event-calendar-file")
	public Object getList(
			@RequestParam(name = "time", required = true, defaultValue = "0") long time, 
			@RequestParam(name = "organizationId", required = true) String organizationId,
			@RequestParam(name = "userId", required = false) String userId) {
		ResponseAPI responseAPI=new ResponseAPI();
		EventCalendarFileFilter eventCalendarFilter=new EventCalendarFileFilter();
		eventCalendarFilter.setTime(time);
		
		CreatorFilter creatorFilter=new CreatorFilter();
		creatorFilter.setOrganizationId(organizationId);
		creatorFilter.setOrganizationUserId(userId);
		eventCalendarFilter.setCreatorFilter(creatorFilter);
		eventCalendarFilter.setTrash(false);
		
		OrderByFilter orderByFilter=new OrderByFilter();
		orderByFilter.add("createdTime", Direction.DESC);
		eventCalendarFilter.setOrderByFilter(orderByFilter);
		
		long total=eventCalendarFileService.countAll(eventCalendarFilter);
		List<EventCalendarFile> eventCalendarFiles=eventCalendarFileService.findAll(eventCalendarFilter);
		List<Document> results=new ArrayList<Document>();
		for (EventCalendarFile item : eventCalendarFiles) {
			results.add(eventCalendarFileUtil.toSiteResponse(item));
		}
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(results);
		return responseAPI.build();
	}
	
	@GetMapping("/event-calendar-file/{id}")
	public Object get(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		EventCalendarFileFilter eventCalendarFileFilter=new EventCalendarFileFilter();
		eventCalendarFileFilter.setId(id);
		eventCalendarFileFilter.setTrash(false);
		
		EventCalendarFile eventCalendarFile=eventCalendarFileService.getOne(eventCalendarFileFilter);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(eventCalendarFileUtil.toSiteResponse(eventCalendarFile));
		return responseAPI.build();
	}
	
	@PostMapping("/event-calendar-file")
	public Object create(@RequestBody @Valid ReqEventCalendarFileCreate reqEventCalendarFileCreate) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		EventCalendarFile eventCalendarFile=eventCalendarFileService.create(reqEventCalendarFileCreate);
		responseAPI.setStatus(HttpStatus.CREATED);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(eventCalendarFileUtil.toSiteResponse(eventCalendarFile));
		return responseAPI.build();
	}
	
	@PutMapping("/event-calendar-file/{id}")
	public Object update(
			@PathVariable(name = "id", required = true) String id,
			@RequestBody @Valid ReqEventCalendarFileUpdate reqEventCalendarFileUpdate) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		EventCalendarFile eventCalendarFile=eventCalendarFileService.update(id, reqEventCalendarFileUpdate);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(eventCalendarFileUtil.toSiteResponse(eventCalendarFile));
		return responseAPI.build();
	}
	
	@DeleteMapping("/event-calendar-file/{id}")
	public Object delete(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		eventCalendarFileService.deleteById(id);
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Đã xóa thành công");
		return responseAPI.build();
	}
}

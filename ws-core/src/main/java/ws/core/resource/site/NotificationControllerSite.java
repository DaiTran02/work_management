package ws.core.resource.site;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import ws.core.enums.NotificationAction;
import ws.core.enums.NotificationObject;
import ws.core.enums.NotificationScope;
import ws.core.enums.NotificationType;
import ws.core.model.Notification;
import ws.core.model.filter.NotificationFilter;
import ws.core.model.filter.ReceiverFilter;
import ws.core.model.filter.SkipLimitFilter;
import ws.core.model.response.ResponseAPI;
import ws.core.model.response.util.NotificationUtil;
import ws.core.services.NotificationService;

@RestController
@RequestMapping("/api/site")
public class NotificationControllerSite {

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private NotificationUtil notificationUtil;
	
	@GetMapping(value = "/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> stream() {
		System.out.println("Goi event ne: ");
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> {
                	System.out.println("event: "+sequence);
                	return ServerSentEvent.<String>builder()
                        .id(String.valueOf(sequence))
                        .event("message")
                        .data("Event #" + sequence + " at " + LocalDateTime.now())
                        .build();
                	}
                );
    }
	
	@GetMapping(value = "/notifications/list")
    public Object list(
    		@RequestParam(name = "skip", required = true) int skip, 
			@RequestParam(name = "limit", required = true) int limit, 
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "organizationUserId", required = false) String organizationUserId,
			@RequestParam(name = "viewed", required = false) Boolean viewed) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		ReceiverFilter receiverFilter=new ReceiverFilter();
		receiverFilter.setOrganizationId(organizationId);
		receiverFilter.setOrganizationUserId(organizationUserId);
		
		NotificationFilter notificationFilter=new NotificationFilter();
		notificationFilter.setFromDate(fromDate);
		notificationFilter.setToDate(toDate);
		notificationFilter.setViewed(viewed);
		notificationFilter.setReceiverFilter(receiverFilter);
		
		notificationFilter.setScope(NotificationScope.organization);
		if(organizationUserId!=null) {
			notificationFilter.setScope(NotificationScope.user);
		}
		notificationFilter.setSkipLimitFilter(new SkipLimitFilter(skip, limit));
		
		long total=notificationService.countAll(notificationFilter);
		List<Notification> notifications=notificationService.findAll(notificationFilter);
		List<Document> result=notifications.stream().map(e->notificationUtil.toResponse(e)).collect(Collectors.toList());
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		responseAPI.setResult(result);
		return responseAPI.build();
    }
	
	@GetMapping(value = "/notifications/count")
    public Object count(
			@RequestParam(name = "fromDate", required = true, defaultValue = "0") long fromDate, 
			@RequestParam(name = "toDate", required = true, defaultValue = "0") long toDate, 
			@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "organizationUserId", required = false) String organizationUserId,
			@RequestParam(name = "viewed", required = false) Boolean viewed) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		ReceiverFilter receiverFilter=new ReceiverFilter();
		receiverFilter.setOrganizationId(organizationId);
		receiverFilter.setOrganizationUserId(organizationUserId);
		
		NotificationFilter notificationFilter=new NotificationFilter();
		notificationFilter.setFromDate(fromDate);
		notificationFilter.setToDate(toDate);
		notificationFilter.setViewed(viewed);

		notificationFilter.setScope(NotificationScope.organization);
		if(organizationUserId!=null) {
			notificationFilter.setScope(NotificationScope.user);
		}
		notificationFilter.setReceiverFilter(receiverFilter);
		
		long total=notificationService.countAll(notificationFilter);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setTotal(total);
		return responseAPI.build();
    }
	
	@GetMapping(value = "/notifications/{id}")
    public Object details(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		Notification notification=notificationService.getById(id);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(notificationUtil.toResponse(notification));
		return responseAPI.build();
    }
	
	@PutMapping(value = "/notifications/{id}/mark-viewed")
    public Object markViewed(@PathVariable(name = "id", required = true) String id) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		notificationService.setViewed(id);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		return responseAPI.build();
	}
	
	@PutMapping(value = "/notifications/mark-all-viewed")
    public Object markAllViewed(
    		@RequestParam(name = "organizationId", required = true) String organizationId, 
			@RequestParam(name = "organizationUserId", required = false) String organizationUserId) {
		ResponseAPI responseAPI=new ResponseAPI();
		
		ReceiverFilter receiverFilter=new ReceiverFilter();
		receiverFilter.setOrganizationId(organizationId);
		receiverFilter.setOrganizationUserId(organizationUserId);
		
		NotificationFilter notificationFilter=new NotificationFilter();
		notificationFilter.setReceiverFilter(receiverFilter);
		
		notificationFilter.setScope(NotificationScope.organization);
		if(organizationUserId!=null) {
			notificationFilter.setScope(NotificationScope.user);
		}
		notificationService.setMarkAllViewed(notificationFilter);
		
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		return responseAPI.build();
    }
	
	@GetMapping("/notifications/scope")
	public Object getScope() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(NotificationScope.values());
		return responseAPI.build();
	}
	
	@GetMapping("/notifications/type")
	public Object getType() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(NotificationType.values());
		return responseAPI.build();
	}
	
	@GetMapping("/notifications/action")
	public Object getAction() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(NotificationAction.values());
		return responseAPI.build();
	}
	
	@GetMapping("/notifications/object")
	public Object getObject() {
		ResponseAPI responseAPI=new ResponseAPI();
		responseAPI.setStatus(HttpStatus.OK);
		responseAPI.setMessage("Thành công");
		responseAPI.setResult(NotificationObject.values());
		return responseAPI.build();
	}
}

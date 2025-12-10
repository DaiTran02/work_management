package ws.core.services;

import java.util.List;
import java.util.Optional;

import ws.core.model.Notification;
import ws.core.model.filter.NotificationFilter;
import ws.core.model.request.ReqNotificationCreate;

public interface NotificationService {
	public long countAll(NotificationFilter notificationFilter);
	
	public List<Notification> findAll(NotificationFilter notificationFilter);
	
	public Optional<Notification> findById(String id);
	
	public Notification getById(String id);
	
	public Notification deleteById(String id);
	
	public Notification create(ReqNotificationCreate reqNotificationCreate);
	
	public Notification setViewed(String notificationId);
	
	public Notification save(Notification notification);
	
	public long setMarkAllViewed(NotificationFilter notificationFilter);
}

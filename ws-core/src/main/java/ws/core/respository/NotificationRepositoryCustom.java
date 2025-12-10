package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.Notification;
import ws.core.model.filter.NotificationFilter;

public interface NotificationRepositoryCustom {
	public List<Notification> findAll(NotificationFilter notificationFilter);
	public long countAll(NotificationFilter notificationFilter);
	public Optional<Notification> findOne(NotificationFilter notificationFilter);
	public long setMarkAll(NotificationFilter notificationFilter);
}

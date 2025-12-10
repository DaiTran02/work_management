package ws.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.Notification;
import ws.core.model.filter.NotificationFilter;
import ws.core.model.request.ReqNotificationCreate;
import ws.core.respository.NotificationRepository;
import ws.core.respository.NotificationRepositoryCustom;
import ws.core.services.NotificationService;

@Service
public class NotificationServiceIml implements NotificationService{

	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private NotificationRepositoryCustom notificationRepositoryCustom;
	
	@Override
	public long countAll(NotificationFilter notificationFilter) {
		return notificationRepositoryCustom.countAll(notificationFilter);
	}

	@Override
	public List<Notification> findAll(NotificationFilter notificationFilter) {
		return notificationRepositoryCustom.findAll(notificationFilter);
	}

	@Override
	public Optional<Notification> findById(String id) {
		return notificationRepository.findById(new ObjectId(id));
	}

	@Override
	public Notification getById(String id) {
		Optional<Notification> findNotification = findById(id);
		if(findNotification.isPresent()) {
			return findNotification.get();
		}
		throw new NotFoundElementExceptionAdvice("notificationId ["+id+"] không tồn tại trong hệ thống");
	}

	@Override
	public Notification deleteById(String id) {
		Notification notification=getById(id);
		notificationRepository.delete(notification);
		return notification;
	}

	@Override
	public Notification create(@Valid ReqNotificationCreate reqNotificationCreate) {
		Notification notification=new Notification();
		notification.setTitle(reqNotificationCreate.getTitle());
		notification.setContent(reqNotificationCreate.getContent());
		notification.setType(reqNotificationCreate.getType());
		notification.setAction(reqNotificationCreate.getAction());
		notification.setActionUrl(reqNotificationCreate.getActionUrl());
		notification.setObject(reqNotificationCreate.getObject());
		notification.setObjectId(reqNotificationCreate.getObjectId());
		notification.setCreator(reqNotificationCreate.getCreator());
		notification.setReceiver(reqNotificationCreate.getReceiver());
		notification.setViewed(false);
		notification.setViewedTime(null);
		notification.setScope(reqNotificationCreate.getScope());
		notification.setMetaDatas(reqNotificationCreate.getMetaDatas());
		notification=notificationRepository.save(notification);
		return notification;
	}

	@Override
	public Notification setViewed(String notificationId) {
		Notification notification = getById(notificationId);
		if(notification.isViewed()==false) {
			notification.setViewed(true);
			notification.setViewedTime(new Date());
			notification=notificationRepository.save(notification);
		}
		return notification;
	}

	@Override
	public Notification save(Notification notification) {
		return notificationRepository.save(notification);
	}

	@Override
	public long setMarkAllViewed(NotificationFilter notificationFilter) {
		return notificationRepositoryCustom.setMarkAll(notificationFilter);
	}

	
}

package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.Notification;

@Component
public class NotificationUtil {
	
	private Document toCommon(Notification notification) {
		Document document=new Document();
		document.put("id", notification.getId());
		document.put("createdTime", notification.getCreatedTimeLong());
		document.put("updatedTime", notification.getUpdatedTimeLong());
		document.put("title", notification.getTitle());
		document.put("content", notification.getContent());
		document.put("type", notification.getType());
		document.put("action", notification.getAction());
		document.put("actionUrl", notification.getActionUrl());
		document.put("object", notification.getObject());
		document.put("objectId", notification.getObjectId());
		document.put("creator", notification.getCreator());
		document.put("receiver", notification.getReceiver());
		document.put("viewed", notification.isViewed());
		document.put("viewedTime", notification.getViewedTimeLong());
		document.put("scope", notification.getScope());
		document.put("metaDatas", notification.getMetaDatas());
		return document;
	}
	
	public Document toResponse(Notification notification) {
		Document document=toCommon(notification);
		return document;
	}
}

package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.EventResource;

@Component
public class EventResourceUtil {
	
	public Document toCommon(EventResource eventResource) {
		Document document=new Document();
		document.append("id", eventResource.getId());
		document.append("createdTime", eventResource.getCreatedTime());
		document.append("updatedTime", eventResource.getUpdatedTime());
		document.append("type", eventResource.getType());
		document.append("name", eventResource.getName());
		document.append("description", eventResource.getDescription());
		document.append("group", eventResource.getGroup());
		document.append("creator", eventResource.getCreator());
		return document;
	}
	
	public Document toSiteResponse(EventResource eventResource) {
		return toCommon(eventResource);
	}
}

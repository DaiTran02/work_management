package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.Configuration;

@Component
public class ConfigurationUtil {
	
	private Document toCommon(Configuration configuration) {
		Document document=new Document();
		document.append("id", configuration.getId());
		document.append("createdTime", configuration.getCreatedTimeLong());
		document.append("updatedTime", configuration.getUpdatedTimeLong());
		document.append("object", configuration.getObject());
		document.append("key", configuration.getKey());
		document.append("name", configuration.getName());
		document.append("description", configuration.getDescription());
		document.append("type", configuration.getType());
		document.append("value", configuration.getValue());
		document.append("active", configuration.isActive());
		document.append("orderNumber", configuration.getOrderNumber());
		return document;
	}
	
	public Document toAdminResponse(Configuration configuration) {
		return toCommon(configuration);
	}
}

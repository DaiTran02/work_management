package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.ClassifyTask;

@Component
public class ClassifyTaskUtil {
	
	private Document toCommon(ClassifyTask classifyTask) {
		Document document=new Document();
		document.put("id", classifyTask.getId());
		document.put("createdTime", classifyTask.getCreatedTimeLong());
		document.put("updatedTime", classifyTask.getUpdatedTimeLong());
		document.put("name", classifyTask.getName());
		document.put("organizationId", classifyTask.getOrganizationId());
		document.put("organizationName", classifyTask.getOrganizationName());
		document.put("order", classifyTask.getOrder());
		document.put("active", classifyTask.isActive());
		document.put("creator", classifyTask.getCreator());
		return document;
	}
	
	public Document toSiteResponse(ClassifyTask classifyTask) {
		Document document=toCommon(classifyTask);
		return document;
	}
}

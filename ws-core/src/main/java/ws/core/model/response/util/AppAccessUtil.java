package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.AppAccess;

@Component
public class AppAccessUtil {
	
	private Document toCommon(AppAccess appAccess) {
		Document document=new Document();
		document.put("id", appAccess.getId());
		document.put("createdTime", appAccess.getCreatedTimeLong());
		document.put("updatedTime", appAccess.getUpdatedTimeLong());
		document.put("apiKey", appAccess.getApiKey());
		document.put("startTime", appAccess.getStartTimeLong());
		document.put("endTime", appAccess.getEndTimeLong());
		document.put("name", appAccess.getName());
		document.put("description", appAccess.getDescription());
		document.put("creatorId", appAccess.getCreatorId());
		document.put("creatorName", appAccess.getCreatorName());
		document.put("organizationId", appAccess.getOrganizationId());
		document.put("ipsAccess", appAccess.getIpsAccess());
		document.put("active", appAccess.isActive());
		return document;
	}
	
	public Document toAdminResponse(AppAccess appAccess) {
		Document document=toCommon(appAccess);
		return document;
	}
	
	public Document toSiteResponse(AppAccess appAccess) {
		Document document=toCommon(appAccess);
		return document;
	}
}

package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.RoleTemplate;

@Component
public class RoleTemplateUtil {
	
	private Document toCommon(RoleTemplate roleTemplate) {
		Document document=new Document();
		document.put("id", roleTemplate.getId());
		document.put("createdTime", roleTemplate.getCreatedTimeLong());
		document.put("updatedTime", roleTemplate.getUpdatedTimeLong());
		document.put("name", roleTemplate.getName());
		document.put("description", roleTemplate.getDescription());
		document.put("creatorId", roleTemplate.getCreatorId());
		document.put("creatorName", roleTemplate.getCreatorName());
		document.put("permissionKeys", roleTemplate.getPermissionKeys());
		document.put("active", roleTemplate.isActive());
		return document;
	}
	
	public Document toAdminResponse(RoleTemplate roleTemplate) {
		Document document=toCommon(roleTemplate);
		return document;
	}
}

package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.Permission;

@Component
public class PermissionUtil {
	
	private Document toCommon(Permission permission) {
		Document document=new Document();
		document.append("id", permission.getId());
		document.append("key", permission.getKey());
		document.append("name", permission.getName());
		document.append("description", permission.getDescription());
		document.append("orderSort", permission.getOrder());
		
		document.append("groupId", permission.getGroupId());
		document.append("groupName", permission.getGroupName());
		document.append("groupSort", permission.getGroupOrder());
		return document;
	}
	
	public Document toAdminResponse(Permission permission) {
		Document document=toCommon(permission);
		return document;
	}
	
	public Document toSiteResponse(Permission permission) {
		Document document=toCommon(permission);
		return document;
	}
}

package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.embeded.RoleOrganizationExpand;

@Component
public class RoleOrganizationExpandUtil {
	
	public Document convertRoleOrganizationExpand(RoleOrganizationExpand roleOrganizationExpand) {
		Document document=new Document();
		document.put("roleId", roleOrganizationExpand.getRoleId());
		document.put("createdTime", roleOrganizationExpand.getCreatedTimeLong());
		document.put("updatedTime", roleOrganizationExpand.getUpdatedTimeLong());
		document.put("name", roleOrganizationExpand.getName());
		document.put("description", roleOrganizationExpand.getDescription());
		document.put("creatorId", roleOrganizationExpand.getCreatorId());
		document.put("creatorName", roleOrganizationExpand.getCreatorName());
		document.put("permissionKeys", roleOrganizationExpand.getPermissionKeys());
		document.put("userIds", roleOrganizationExpand.getUserIds());
		document.put("roleTemplateId", roleOrganizationExpand.getRoleTemplateId());
		document.put("type", roleOrganizationExpand.getType());
		document.put("active", roleOrganizationExpand.isActive());
		document.put("archive", roleOrganizationExpand.isArchive());
		return document;
	}
}

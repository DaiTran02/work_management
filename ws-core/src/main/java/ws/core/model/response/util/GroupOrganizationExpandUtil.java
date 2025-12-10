package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.embeded.GroupOrganizationExpand;

@Component
public class GroupOrganizationExpandUtil {
	
	public Document convertGroupOrganizationExpand(GroupOrganizationExpand groupOrganizationExpand) {
		Document document=new Document();
		document.put("groupId", groupOrganizationExpand.getGroupId());
		document.put("createdTime", groupOrganizationExpand.getCreatedTimeLong());
		document.put("updatedTime", groupOrganizationExpand.getUpdatedTimeLong());
		document.put("name", groupOrganizationExpand.getName());
		document.put("description", groupOrganizationExpand.getDescription());
		document.put("creatorId", groupOrganizationExpand.getCreatorId());
		document.put("creatorName", groupOrganizationExpand.getCreatorName());
		document.put("userIds", groupOrganizationExpand.getUserIds());
		document.put("active", groupOrganizationExpand.isActive());
		document.put("archive", groupOrganizationExpand.isArchive());
		return document;
	}
}

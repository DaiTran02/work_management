package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.stereotype.Component;

import ws.core.model.LeaderApproveTask;

@Component
public class LeaderApproveTaskUtil {
	
	private Document toCommon(LeaderApproveTask leaderApproveTask) {
		Document document=new Document();
		document.put("id", leaderApproveTask.getId());
		document.put("createdTime", leaderApproveTask.getCreatedTimeLong());
		document.put("updatedTime", leaderApproveTask.getUpdatedTimeLong());
		document.put("name", leaderApproveTask.getName());
		document.put("organizationId", leaderApproveTask.getOrganizationId());
		document.put("organizationName", leaderApproveTask.getOrganizationName());
		document.put("order", leaderApproveTask.getOrder());
		document.put("active", leaderApproveTask.isActive());
		document.put("creator", leaderApproveTask.getCreator());
		return document;
	}
	
	public Document toSiteResponse(LeaderApproveTask leaderApproveTask) {
		Document document=toCommon(leaderApproveTask);
		return document;
	}
}

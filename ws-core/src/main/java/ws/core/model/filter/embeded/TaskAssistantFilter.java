package ws.core.model.filter.embeded;

import java.util.List;

import lombok.Data;

@Data
public class TaskAssistantFilter {
	private String organizationId=null;
	private List<String> organizationIds=null;
	private String organizationGroupId=null;
	private String organizationUserId=null;
}

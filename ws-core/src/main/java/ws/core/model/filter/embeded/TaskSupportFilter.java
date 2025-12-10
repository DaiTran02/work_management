package ws.core.model.filter.embeded;

import java.util.List;

import lombok.Data;

@Data
public class TaskSupportFilter {
	private String organizationId=null;
	private List<String> organizationIds=null;
	private String organizationUserId=null;
}

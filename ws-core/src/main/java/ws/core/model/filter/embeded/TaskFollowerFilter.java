package ws.core.model.filter.embeded;

import java.util.List;

import lombok.Data;

@Data
public class TaskFollowerFilter {
	private String organizationId=null;
	private List<String> organizationIds=null;
	private String organizationGroupId=null;
	private String organizationUserId=null;
}

package ws.core.model.filter.embeded;

import lombok.Data;

@Data
public class TaskUserRefFilter {
	private String organizationId=null;
	private String organizationUserId=null;
}

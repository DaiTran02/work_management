package ws.core.model.filter;

import java.util.List;

import lombok.Data;

@Data
public class ReceiverFilter {
	private String organizationId;
	private List<String> organizationIds=null;
	private String organizationUserId;
}

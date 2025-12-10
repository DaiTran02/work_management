package ws.core.model.filter;

import java.util.List;

import lombok.Data;

@Data
public class CreatorFilter {
	private String organizationId;
	private List<String> organizationIds=null;
	private String organizationUserId;
}

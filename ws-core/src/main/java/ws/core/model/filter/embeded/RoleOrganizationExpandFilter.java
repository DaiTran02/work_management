package ws.core.model.filter.embeded;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleOrganizationExpandFilter {
	private String roleTemplateId;
}

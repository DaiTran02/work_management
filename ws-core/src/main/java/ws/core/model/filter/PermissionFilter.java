package ws.core.model.filter;

import lombok.Data;

@Data
public class PermissionFilter {
	private SkipLimitFilter skipLimitFilter=null;
	private OrderByFilter orderByFilter=null;
}

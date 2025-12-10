package ws.core.model.filter;

import lombok.Data;

@Data
public class UpgradeFilter {
	private String name=null;
	
	private SkipLimitFilter skipLimitFilter=null;
	private OrderByFilter orderByFilter=null;
}

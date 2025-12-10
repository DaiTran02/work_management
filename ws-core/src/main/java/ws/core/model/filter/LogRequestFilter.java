package ws.core.model.filter;

import lombok.Data;

@Data
public class LogRequestFilter {
	private long fromDate=0;
	private long toDate=0;
	private String keySearch=null;
	
	private String organizationId=null;
	private String userId=null;
	
	private SkipLimitFilter skipLimitFilter=null;
	private OrderByFilter orderByFilter=null;
}

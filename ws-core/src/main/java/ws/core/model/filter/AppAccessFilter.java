package ws.core.model.filter;

import lombok.Data;

@Data
public class AppAccessFilter {
	private long fromDate=0;
	private long toDate=0;
	private String apiKey=null;
	private String keySearch=null;
	private String ipAccess=null;
	private String active=null;
	private String organizationId=null;
	
	private SkipLimitFilter skipLimitFilter=null;
	private OrderByFilter orderByFilter=null;
}

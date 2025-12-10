package ws.core.model.filter;

import lombok.Data;

@Data
public class ClassifyTaskFilter {
	private long fromDate=0;
	private long toDate=0;
	private String organizationId=null;
	private String keySearch=null;
	private Boolean active=null;
	
	private SkipLimitFilter skipLimitFilter=null;
	private OrderByFilter orderByFilter=null;
}

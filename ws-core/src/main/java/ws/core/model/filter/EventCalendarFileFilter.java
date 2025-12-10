package ws.core.model.filter;

import lombok.Data;

@Data
public class EventCalendarFileFilter {
	private String id;
	private long time;
	private Boolean trash;
	private CreatorFilter creatorFilter;
	
	private SearchingTypeFilter searchingTypeFilter;
	private SkipLimitFilter skipLimitFilter;
	private OrderByFilter orderByFilter;
}

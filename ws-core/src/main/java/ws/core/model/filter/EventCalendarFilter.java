package ws.core.model.filter;

import lombok.Data;

@Data
public class EventCalendarFilter {
	private String id;
	private String keyword;
	private long createdFrom;
	private long createdTo;
	private long fromDate;
	private long toDate;
	private Boolean trash;
	private String type;
	private String status;
	private Boolean excludeCreator;
	
	private CreatorFilter creatorFilter;
	
	/**
	 * Lịch sắp đến giờ họp, cụ thể trước bao nhiêu phút
	 */
	private long beforeEventTime;
	
	private SearchingTypeFilter searchingTypeFilter;
	private SkipLimitFilter skipLimitFilter;
	private OrderByFilter orderByFilter;
}

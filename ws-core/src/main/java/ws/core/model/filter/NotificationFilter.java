package ws.core.model.filter;

import lombok.Data;
import ws.core.enums.NotificationScope;

@Data
public class NotificationFilter {
	private long fromDate=0;
	private long toDate=0;
	private String keySearch=null;
	private String type=null;
	private String action=null;
	private String classId=null;
	private CreatorFilter creatorFilter=null;
	private ReceiverFilter receiverFilter=null;
	private Boolean viewed=null;
	private NotificationScope scope=null;
	
	private SearchingTypeFilter searchingTypeFilter=null;
	private SkipLimitFilter skipLimitFilter=null;
	private OrderByFilter orderByFilter=null;
}

package ws.core.model.filter;

import java.util.List;

import lombok.Data;

@Data
public class TagFilter {
	private List<String> ids=null;
	private String type=null;
	private List<String> classIds=null;
	private Boolean active=null;
	private Boolean archive=null;
	private CreatorFilter creatorFilter=null;
	
	private SearchingTypeFilter searchingTypeFilter=null;
	private String keySearch=null;
	
	private SkipLimitFilter skipLimitFilter=null;
	private OrderByFilter orderByFilter=null;
}
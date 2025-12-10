package ws.core.model.filter;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class EventResourceFilter {
	private String id;
	private List<String> ids=new ArrayList<String>();
	private String keyword;
	private String type;
	private Integer group;
	private Boolean trash;
	private CreatorFilter creatorFilter;
	
	private SearchingTypeFilter searchingTypeFilter;
	private SkipLimitFilter skipLimitFilter;
	private OrderByFilter orderByFilter;
}

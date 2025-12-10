package ws.core.model.filter;

import lombok.Data;

@Data
public class ConfigurationFilter {
	private String id=null;
	private String key=null;
	private String keySearch=null;
	private Boolean active=null;
	
	private SearchingTypeFilter searchingTypeFilter;
	private SkipLimitFilter skipLimitFilter;
	private OrderByFilter orderByFilter;
}

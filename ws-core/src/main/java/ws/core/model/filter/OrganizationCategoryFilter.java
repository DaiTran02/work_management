package ws.core.model.filter;

import lombok.Data;

@Data
public class OrganizationCategoryFilter {
	private String keySearch=null;
	private Boolean active=null;
	private SkipLimitFilter skipLimitFilter=null;
	private OrderByFilter orderByFilter=null;
}

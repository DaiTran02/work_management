package ws.core.model.filter;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import ws.core.model.filter.embeded.RoleOrganizationExpandFilter;

@Data
public class OrganizationFilter {
	private List<String> ids=null;
	private List<String> unitCodes=null;
	
	private Boolean active=null;
	private boolean root=false;
	private String parentId=null;
	private String level=null;
	
	private String includeUserId=null;
	private String organizationCategoryId=null;
	private Boolean hasContainUsers=null;
	
	private RoleOrganizationExpandFilter roleOrganizationExpandFilter=null;

	private String keySearch=null;
	private SearchingTypeFilter searchingTypeFilter=null;
	
	private SkipLimitFilter skipLimitFilter=null;
	
	private OrderByFilter orderByFilter=null;
	
	public void setId(String id) {
		ids=new ArrayList<>();
		ids.add(id);
	}
}

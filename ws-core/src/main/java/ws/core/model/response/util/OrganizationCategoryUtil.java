package ws.core.model.response.util;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ws.core.model.OrganizationCategory;
import ws.core.model.filter.OrganizationFilter;
import ws.core.services.OrganizationService;

@Component
public class OrganizationCategoryUtil {
	
	@Autowired
	private OrganizationService organizationService;
	
	private Document toCommon(OrganizationCategory organizationCategory) {
		Document document=new Document();
		document.append("id", organizationCategory.getId());
		document.append("name", organizationCategory.getName());
		document.append("description", organizationCategory.getDescription());
		document.append("active", organizationCategory.isActive());
		document.append("order", organizationCategory.getOrder());
		return document;
	}
	
	public Document toAdminResponse(OrganizationCategory organizationCategory) {
		Document document=toCommon(organizationCategory);
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setOrganizationCategoryId(organizationCategory.getId());
		long count=organizationService.countOrganizationAll(organizationFilter);
		document.append("count", count);
		
		return document;
	}
	
	public Document toSiteResponse(OrganizationCategory organizationCategory) {
		Document document=toCommon(organizationCategory);
		
		OrganizationFilter organizationFilter=new OrganizationFilter();
		organizationFilter.setOrganizationCategoryId(organizationCategory.getId());
		long count=organizationService.countOrganizationAll(organizationFilter);
		document.append("count", count);
		
		return document;
	}
}

package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.OrganizationCategory;
import ws.core.model.filter.OrganizationCategoryFilter;

public interface OrganizationCategoryRepositoryCustom {
	List<OrganizationCategory> findAll(OrganizationCategoryFilter organizationCategoryFilter);
	long countAll(OrganizationCategoryFilter organizationCategoryFilter);
	Optional<OrganizationCategory> findOne(OrganizationCategoryFilter organizationCategoryFilter);
}

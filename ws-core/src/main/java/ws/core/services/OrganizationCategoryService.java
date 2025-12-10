package ws.core.services;

import java.util.List;
import java.util.Optional;

import ws.core.model.OrganizationCategory;
import ws.core.model.filter.OrganizationCategoryFilter;
import ws.core.model.request.ReqOrganizationCategoryCreate;
import ws.core.model.request.ReqOrganizationCategoryUpdate;

public interface OrganizationCategoryService {
	public long countAll(OrganizationCategoryFilter leaderApproveTaskFilter);
	
	public List<OrganizationCategory> findAll(OrganizationCategoryFilter leaderApproveTaskFilter);
	
	public Optional<OrganizationCategory> findById(String id);
	
	public OrganizationCategory getById(String id);
	
	public OrganizationCategory deleteById(String id);

	public OrganizationCategory create(ReqOrganizationCategoryCreate reqOrganizationCategoryCreate);

	public OrganizationCategory update(String leaderApproveTaskId, ReqOrganizationCategoryUpdate reqOrganizationCategoryUpdate);
}

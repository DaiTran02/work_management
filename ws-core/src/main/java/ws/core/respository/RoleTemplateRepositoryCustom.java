package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.RoleTemplate;
import ws.core.model.filter.RoleTemplateFilter;

public interface RoleTemplateRepositoryCustom {
	List<RoleTemplate> findAll(RoleTemplateFilter roleTemplateFilter);
	long countAll(RoleTemplateFilter roleTemplateFilter);
	Optional<RoleTemplate> findOne(RoleTemplateFilter roleTemplateFilter);
}

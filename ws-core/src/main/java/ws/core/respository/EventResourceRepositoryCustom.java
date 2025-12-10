package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.EventResource;
import ws.core.model.filter.EventResourceFilter;

public interface EventResourceRepositoryCustom{
	List<EventResource> findAll(EventResourceFilter eventResourceFilter);
	long countAll(EventResourceFilter eventResourceFilter);
	Optional<EventResource> findOne(EventResourceFilter eventResourceFilter);
}

package ws.core.services;

import java.util.List;
import java.util.Optional;

import ws.core.enums.DataAction;
import ws.core.model.EventResource;
import ws.core.model.User;
import ws.core.model.filter.EventResourceFilter;
import ws.core.model.request.ReqEventResourceCreate;
import ws.core.model.request.ReqEventResourceUpdate;

public interface EventResourceService {
	public long countAll(EventResourceFilter eventResourceFilter);
	public List<EventResource> findAll(EventResourceFilter eventResourceFilter);
	public Optional<EventResource> findOne(EventResourceFilter eventResourceFilter);
	public EventResource getOne(EventResourceFilter eventResourceFilter);
	public Optional<EventResource> findById(String id);
	public EventResource getById(String id);
	public EventResource create(ReqEventResourceCreate reqEventResourceCreate, User actor);
	public EventResource update(String id, ReqEventResourceUpdate reqEventResourceUpdate, User actor);
	public EventResource delete(String id, User actor);
	public EventResource save(EventResource eventResource, DataAction dataAction, User user);
}

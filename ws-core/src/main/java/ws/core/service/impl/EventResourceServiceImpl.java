package ws.core.service.impl;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ws.core.advice.DuplicateKeyExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.enums.DataAction;
import ws.core.model.EventResource;
import ws.core.model.User;
import ws.core.model.filter.EventResourceFilter;
import ws.core.model.request.ReqEventResourceCreate;
import ws.core.model.request.ReqEventResourceUpdate;
import ws.core.respository.EventResourceRepository;
import ws.core.respository.EventResourceRepositoryCustom;
import ws.core.services.EventResourceService;

@Service
public class EventResourceServiceImpl implements EventResourceService{
	@Autowired
	private EventResourceRepository eventResourceRepository;

	@Autowired
	private EventResourceRepositoryCustom eventResourceRepositoryCustom;
	
	@Override
	public long countAll(EventResourceFilter eventResourceFilter) {
		return eventResourceRepositoryCustom.countAll(eventResourceFilter);
	}

	@Override
	public List<EventResource> findAll(EventResourceFilter eventResourceFilter) {
		return eventResourceRepositoryCustom.findAll(eventResourceFilter);
	}

	@Override
	public Optional<EventResource> findOne(EventResourceFilter eventResourceFilter) {
		return eventResourceRepositoryCustom.findOne(eventResourceFilter);
	}

	@Override
	public EventResource getOne(EventResourceFilter eventResourceFilter) {
		Optional<EventResource> findEventResource=findOne(eventResourceFilter);
		if(findEventResource.isPresent()) {
			return findEventResource.get();
		}
		throw new NotFoundElementExceptionAdvice("eventResource không tồn tại trong hệ thống");
	}

	@Override
	public Optional<EventResource> findById(String id) {
		return eventResourceRepository.findById(new ObjectId(id));
	}

	@Override
	public EventResource getById(String id) {
		Optional<EventResource> findEventResource=findById(id);
		if(findEventResource.isPresent()) {
			return findEventResource.get();
		}
		throw new NotFoundElementExceptionAdvice("eventResource ["+id+"] không tồn tại trong hệ thống");
	}

	@Override
	public EventResource create(ReqEventResourceCreate reqEventResourceCreate, User user) {
		EventResource eventResource = new EventResource();
		eventResource.setType(reqEventResourceCreate.getType());
		eventResource.setName(reqEventResourceCreate.getName());
		eventResource.setDescription(reqEventResourceCreate.getDescription());
		eventResource.setGroup(reqEventResourceCreate.getGroup());
		eventResource.setCreator(reqEventResourceCreate.getCreator().toCreator());
		
		eventResource=save(eventResource, DataAction.create, user);
		return eventResource;
	}

	@Override
	public EventResource update(String id, ReqEventResourceUpdate reqEventResourceUpdate, User user) {
		EventResource eventResource=getById(id);
		
		/* Cập nhật mới */
		eventResource.setName(reqEventResourceUpdate.getName());
		eventResource.setDescription(reqEventResourceUpdate.getDescription());
		
		eventResource=save(eventResource, DataAction.update, user);
		return eventResource;
	}

	@Override
	public EventResource delete(String id, User actor) {
		EventResource eventResource=getById(id);
		if(!eventResource.isTrash()) {
			eventResource.setTrash(true);
			eventResource=save(eventResource, DataAction.delele, actor);
		}
		return eventResource;
	}

	@Override
	public EventResource save(EventResource eventResource, DataAction dataAction, User user) {
		try {
			Assert.notNull(eventResource, "eventResource is null");
			Assert.notNull(dataAction, "dataAction is null");
			
			if(user!=null) {
				eventResource.setActor(user.toActor());
			}
			eventResource = eventResourceRepository.save(eventResource);
			return eventResource;
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}
}

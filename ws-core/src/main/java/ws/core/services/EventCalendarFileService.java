package ws.core.services;

import java.util.List;
import java.util.Optional;

import ws.core.model.EventCalendarFile;
import ws.core.model.filter.EventCalendarFileFilter;
import ws.core.model.request.ReqEventCalendarFileCreate;
import ws.core.model.request.ReqEventCalendarFileUpdate;

public interface EventCalendarFileService {
	
	public long countAll(EventCalendarFileFilter eventCalendarFileFilter);
	
	public List<EventCalendarFile> findAll(EventCalendarFileFilter eventCalendarFileFilter);
	
	public Optional<EventCalendarFile> findOne(EventCalendarFileFilter eventCalendarFileFilter);
	
	public EventCalendarFile getOne(EventCalendarFileFilter eventCalendarFileFilter);
	
	public Optional<EventCalendarFile> findById(String id);
	
	public EventCalendarFile getById(String id);
	
	public EventCalendarFile deleteById(String id);
	
	public EventCalendarFile create(ReqEventCalendarFileCreate eventCalendarFileCreate);
	
	public EventCalendarFile update(String id, ReqEventCalendarFileUpdate eventCalendarFileUpdate);
	
	public EventCalendarFile save(EventCalendarFile eventCalendarFile);
}

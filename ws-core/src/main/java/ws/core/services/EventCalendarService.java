package ws.core.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import ws.core.enums.DataAction;
import ws.core.model.EventCalendar;
import ws.core.model.User;
import ws.core.model.filter.EventCalendarFilter;
import ws.core.model.request.ReqEventCalendarCreate;
import ws.core.model.request.ReqEventCalendarUpdate;
import ws.core.model.request.ReqEventCalendarUserDoConfirmed;
import ws.core.model.request.ReqEventCalendarUserDoDelegacy;

public interface EventCalendarService {

	public long countAll(EventCalendarFilter eventCalendarFilter);
	
	public List<EventCalendar> findAll(EventCalendarFilter eventCalendarFilter);
	
	public Optional<EventCalendar> findOne(EventCalendarFilter eventCalendarFilter);
	
	public EventCalendar getOne(EventCalendarFilter eventCalendarFilter);
	
	public Optional<EventCalendar> findById(String id);
	
	public EventCalendar getById(String id);
	
	public EventCalendar deleteById(String id, User user);
	
	public EventCalendar create(ReqEventCalendarCreate reqEventCalendarCreate, User user);
	
	public EventCalendar update(String id, ReqEventCalendarUpdate eventCalendarFileUpdate, User user);
	
	public EventCalendar save(EventCalendar eventCalendar, DataAction dataAction, User user);

	public void notifySoonExpire(Date beforeEventMinutes);
	
	public EventCalendar userDoConfirm(String eventCalendarId, ReqEventCalendarUserDoConfirmed reqEventCalendarUserDoConfirmed, User user);
	
	public EventCalendar userDoDelegacy(String eventCalendarId, ReqEventCalendarUserDoDelegacy reqEventCalendarUserDoDelegacy, User user);
	
	
}

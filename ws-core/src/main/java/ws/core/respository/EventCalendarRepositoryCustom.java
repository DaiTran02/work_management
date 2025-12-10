package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.EventCalendar;
import ws.core.model.filter.EventCalendarFilter;

public interface EventCalendarRepositoryCustom{
	List<EventCalendar> findAll(EventCalendarFilter eventCalendarFilter);
	long countAll(EventCalendarFilter eventCalendarFilter);
	Optional<EventCalendar> findOne(EventCalendarFilter eventCalendarFilter);
}

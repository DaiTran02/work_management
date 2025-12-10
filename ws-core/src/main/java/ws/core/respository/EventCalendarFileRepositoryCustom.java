package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.EventCalendarFile;
import ws.core.model.filter.EventCalendarFileFilter;

public interface EventCalendarFileRepositoryCustom{
	List<EventCalendarFile> findAll(EventCalendarFileFilter eventCalendarFileFilter);
	long countAll(EventCalendarFileFilter eventCalendarFileFilter);
	Optional<EventCalendarFile> findOne(EventCalendarFileFilter eventCalendarFileFilter);
}

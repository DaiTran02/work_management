package ws.core.respository;

import java.util.List;

import ws.core.model.EventHistory;
import ws.core.model.filter.EventHistoryFilter;

public interface EventHistoryRepositoryCustom{
	List<EventHistory> findAll(EventHistoryFilter eventHistoryFilter, int skip, int limit);
	long countAll(EventHistoryFilter eventHistoryFilter);
}

package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.DocHistory;
import ws.core.model.filter.DocHistoryFilter;

public interface DocHistoryRepositoryCustom {
	List<DocHistory> findAll(DocHistoryFilter docHistoryFilter);
	long countAll(DocHistoryFilter docHistoryFilter);
	Optional<DocHistory> findOne(DocHistoryFilter docHistoryFilter);
}

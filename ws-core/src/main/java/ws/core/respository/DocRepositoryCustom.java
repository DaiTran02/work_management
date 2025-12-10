package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.Doc;
import ws.core.model.filter.DocFilter;

public interface DocRepositoryCustom {
	List<Doc> findAll(DocFilter docFilter);
	long countAll(DocFilter docFilter);
	Optional<Doc> findOne(DocFilter docFilter);
}

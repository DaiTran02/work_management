package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.LogAccess;
import ws.core.model.filter.LogAccessFilter;

public interface LogAccessRepositoryCustom {
	long countAll(LogAccessFilter logAccessFilter);
	List<LogAccess> findAll(LogAccessFilter logAccessFilter);
	List<String> getDistinctUsers(LogAccessFilter logAccessFilter);
	Optional<LogAccess> findOne(LogAccessFilter logAccessFilter);
}

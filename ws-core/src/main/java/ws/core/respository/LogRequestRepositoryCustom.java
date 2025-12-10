package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.LogRequest;
import ws.core.model.filter.LogRequestFilter;

public interface LogRequestRepositoryCustom {
	long countAll(LogRequestFilter logRequestFilter);
	List<LogRequest> findAll(LogRequestFilter logRequestFilter);
	Optional<LogRequest> findOne(LogRequestFilter logRequestFilter);
}

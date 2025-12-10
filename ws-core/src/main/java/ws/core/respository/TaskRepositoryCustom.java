package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.Task;
import ws.core.model.filter.TaskFilter;

public interface TaskRepositoryCustom {
	List<Task> findAll(TaskFilter taskFilter);
	long countAll(TaskFilter taskFilter);
	Optional<Task> findOne(TaskFilter taskFilter);
}

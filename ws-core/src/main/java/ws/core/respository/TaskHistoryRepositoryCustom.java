package ws.core.respository;

import java.util.List;
import java.util.Optional;

import ws.core.model.TaskHistory;
import ws.core.model.filter.TaskHistoryFilter;

public interface TaskHistoryRepositoryCustom {
	List<TaskHistory> findAll(TaskHistoryFilter taskHistoryFilter);
	long countAll(TaskHistoryFilter taskHistoryFilter);
	Optional<TaskHistory> findOne(TaskHistoryFilter taskHistoryFilter);
}

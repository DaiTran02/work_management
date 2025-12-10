package ws.core.services;

import java.util.List;

import ws.core.model.Task;
import ws.core.model.TaskHistory;
import ws.core.model.embeded.Creator;
import ws.core.model.filter.TaskHistoryFilter;

public interface TaskHistoryService {
	public long countTaskHistoryAll(TaskHistoryFilter TaskHistoryFilter);
	
	public List<TaskHistory> findTaskHistoryAll(TaskHistoryFilter TaskHistoryFilter);
	
	public TaskHistory findTaskHistoryById(String id);
	
	public TaskHistory deleteTaskHistoryById(String id);
	
	public TaskHistory saveTaskHistory(Task task, String action, Creator creator);
}

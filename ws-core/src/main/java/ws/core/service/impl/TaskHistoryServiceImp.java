package ws.core.service.impl;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.Task;
import ws.core.model.TaskHistory;
import ws.core.model.embeded.Creator;
import ws.core.model.filter.TaskHistoryFilter;
import ws.core.respository.TaskHistoryRepository;
import ws.core.respository.TaskHistoryRepositoryCustom;
import ws.core.services.TaskHistoryService;

@Service
public class TaskHistoryServiceImp implements TaskHistoryService{

	@Autowired
	private TaskHistoryRepository taskHistoryRepository;
	
	@Autowired
	private TaskHistoryRepositoryCustom taskHistoryRepositoryCustom;

	@Override
	public long countTaskHistoryAll(TaskHistoryFilter TaskHistoryFilter) {
		return taskHistoryRepositoryCustom.countAll(TaskHistoryFilter);
	}

	@Override
	public List<TaskHistory> findTaskHistoryAll(TaskHistoryFilter TaskHistoryFilter) {
		return taskHistoryRepositoryCustom.findAll(TaskHistoryFilter);
	}

	@Override
	public TaskHistory findTaskHistoryById(String id) {
		Optional<TaskHistory> findTask=taskHistoryRepository.findById(new ObjectId(id));
		if(findTask.isPresent()) {
			return findTask.get();
		}
		throw new NotFoundElementExceptionAdvice("taskHistoryId ["+id+"] không tồn tại trong hệ thống");
	}

	@Override
	public TaskHistory deleteTaskHistoryById(String id) {
		TaskHistory task=findTaskHistoryById(id);
		taskHistoryRepository.delete(task);
		return task;
	}

	@Override
	public TaskHistory saveTaskHistory(Task task, String action, Creator creator) {
		TaskHistory taskHistory=new TaskHistory();
		taskHistory.setTaskId(task.getId());
		taskHistory.setTaskData(task);
		taskHistory.setAction(action);
		taskHistory.setCreator(creator);
		return taskHistoryRepository.save(taskHistory);
	}
}

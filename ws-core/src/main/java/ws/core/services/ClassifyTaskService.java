package ws.core.services;

import java.util.List;

import ws.core.model.ClassifyTask;
import ws.core.model.User;
import ws.core.model.filter.ClassifyTaskFilter;
import ws.core.model.request.ReqClassifyTaskCreate;
import ws.core.model.request.ReqClassifyTaskUpdate;

public interface ClassifyTaskService {
	public long countClassifyTaskAll(ClassifyTaskFilter classifyTaskFilter);
	
	public List<ClassifyTask> findClassifyTaskAll(ClassifyTaskFilter classifyTaskFilter);
	
	public ClassifyTask findClassifyTaskById(String id);
	
	public ClassifyTask deleteClassifyTaskById(String id);
	
	public ClassifyTask createClassifyTask(ReqClassifyTaskCreate reqClassifyTaskCreate, User creator);
	
	public ClassifyTask updateClassifyTask(String classifyTaskId, ReqClassifyTaskUpdate reqClassifyTaskUpdate);
}

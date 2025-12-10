package ws.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.model.ClassifyTask;
import ws.core.model.User;
import ws.core.model.filter.ClassifyTaskFilter;
import ws.core.model.filter.DocFilter;
import ws.core.model.request.ReqClassifyTaskCreate;
import ws.core.model.request.ReqClassifyTaskUpdate;
import ws.core.respository.ClassifyTaskRepository;
import ws.core.respository.ClassifyTaskRepositoryCustom;
import ws.core.services.ClassifyTaskService;
import ws.core.services.DocService;

@Service
public class ClassifyTaskServiceImpl implements ClassifyTaskService{

	@Autowired
	private ClassifyTaskRepository classifyTaskRepository;
	
	@Autowired
	private ClassifyTaskRepositoryCustom classifyTaskRepositoryCustom;
	
	@Autowired
	private DocService docService;
	
	@Override
	public long countClassifyTaskAll(ClassifyTaskFilter classifyTaskFilter) {
		return classifyTaskRepositoryCustom.countAll(classifyTaskFilter);
	}

	@Override
	public List<ClassifyTask> findClassifyTaskAll(ClassifyTaskFilter classifyTaskFilter) {
		return classifyTaskRepositoryCustom.findAll(classifyTaskFilter);
	}

	@Override
	public ClassifyTask findClassifyTaskById(String id) {
		Optional<ClassifyTask> findClassifyTask=classifyTaskRepository.findById(new ObjectId(id));
		if(findClassifyTask.isPresent()) {
			return findClassifyTask.get();
		}
		throw new NotFoundElementExceptionAdvice("Không tìm thấy");
	}

	@Override
	public ClassifyTask deleteClassifyTaskById(String id) {
		ClassifyTask classifyTask=findClassifyTaskById(id);
		
		DocFilter docFilter=new DocFilter();
		docFilter.setClassifyTaskId(classifyTask.getId());
		
		if(docService.countDocAll(docFilter)==0) {
			classifyTaskRepository.delete(classifyTask);
			return classifyTask;
		}
		throw new NotAcceptableExceptionAdvice("Không thể xóa, vì dữ liệu đã được sử dụng");
	}

	@Override
	public ClassifyTask createClassifyTask(ReqClassifyTaskCreate reqClassifyTaskCreate, User creator) {
		ClassifyTask classifyTask=new ClassifyTask();
		classifyTask.setName(reqClassifyTaskCreate.getName());
		classifyTask.setOrder(reqClassifyTaskCreate.getOrder());
		classifyTask.setOrganizationId(reqClassifyTaskCreate.getOrganizationId());
		classifyTask.setOrganizationName(reqClassifyTaskCreate.getOrganizationName());
		classifyTask.setCreator(reqClassifyTaskCreate.getCreator().toCreator());
		classifyTask.setActive(reqClassifyTaskCreate.isActive());
		
		return classifyTaskRepository.save(classifyTask);
	}

	@Override
	public ClassifyTask updateClassifyTask(String classifyTaskId, ReqClassifyTaskUpdate reqClassifyTaskUpdate) {
		ClassifyTask classifyTask=findClassifyTaskById(classifyTaskId);
		classifyTask.setUpdatedTime(new Date());
		classifyTask.setName(reqClassifyTaskUpdate.getName());
		classifyTask.setOrder(reqClassifyTaskUpdate.getOrder());
		classifyTask.setActive(reqClassifyTaskUpdate.isActive());
		
		return classifyTaskRepository.save(classifyTask);
	}

}

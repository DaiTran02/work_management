package ws.core.service.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.EnumUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import ws.core.advice.BadRequestExceptionAdvice;
import ws.core.advice.DuplicateKeyExceptionAdvice;
import ws.core.advice.NotAcceptableExceptionAdvice;
import ws.core.advice.NotFoundElementExceptionAdvice;
import ws.core.advice.ObjectIdExceptionAdvance;
import ws.core.enums.TaskAction;
import ws.core.enums.TaskCompleteStatus;
import ws.core.enums.TaskPriority;
import ws.core.enums.TaskState;
import ws.core.model.Doc;
import ws.core.model.Task;
import ws.core.model.User;
import ws.core.model.embeded.Creator;
import ws.core.model.embeded.TaskAssignee;
import ws.core.model.embeded.TaskComment;
import ws.core.model.embeded.TaskCompleted;
import ws.core.model.embeded.TaskConfirmRefuse;
import ws.core.model.embeded.TaskDocInfo;
import ws.core.model.embeded.TaskFollower;
import ws.core.model.embeded.TaskPending;
import ws.core.model.embeded.TaskProcess;
import ws.core.model.embeded.TaskRating;
import ws.core.model.embeded.TaskRedo;
import ws.core.model.embeded.TaskRefuse;
import ws.core.model.embeded.TaskRemind;
import ws.core.model.embeded.TaskReported;
import ws.core.model.embeded.TaskReverse;
import ws.core.model.embeded.TaskSupport;
import ws.core.model.filter.TaskFilter;
import ws.core.model.filter.embeded.TaskUserRefFilter;
import ws.core.model.request.ReqTaskChildCreate;
import ws.core.model.request.ReqTaskCreate;
import ws.core.model.request.ReqTaskDoAccept;
import ws.core.model.request.ReqTaskDoAssignUserAssignee;
import ws.core.model.request.ReqTaskDoAssignUserSupport;
import ws.core.model.request.ReqTaskDoComment;
import ws.core.model.request.ReqTaskDoComplete;
import ws.core.model.request.ReqTaskDoConfirm;
import ws.core.model.request.ReqTaskDoConfirmRefuse;
import ws.core.model.request.ReqTaskDoPending;
import ws.core.model.request.ReqTaskDoRating;
import ws.core.model.request.ReqTaskDoRedo;
import ws.core.model.request.ReqTaskDoRedoAndReportAgain;
import ws.core.model.request.ReqTaskDoRefuse;
import ws.core.model.request.ReqTaskDoRemind;
import ws.core.model.request.ReqTaskDoReport;
import ws.core.model.request.ReqTaskDoReverse;
import ws.core.model.request.ReqTaskDoUnAssignUserAssignee;
import ws.core.model.request.ReqTaskDoUnAssignUserSupport;
import ws.core.model.request.ReqTaskDoUnPending;
import ws.core.model.request.ReqTaskDoUpdateProcess;
import ws.core.model.request.ReqTaskUpdate;
import ws.core.respository.TaskRepository;
import ws.core.respository.TaskRepositoryCustom;
import ws.core.services.DocService;
import ws.core.services.PropsService;
import ws.core.services.TaskEventService;
import ws.core.services.TaskHistoryService;
import ws.core.services.TaskService;
import ws.core.services.UserService;
import ws.core.util.DateTimeUtil;

@Service
public class TaskServiceImpl implements TaskService{

	@Autowired
	private DocService docService;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private TaskRepositoryCustom taskRepositoryCustom;
	
	@Autowired
	private TaskHistoryService taskHistoryService;
	
	@Autowired
	private TaskEventService taskEventService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PropsService propsService;
	
	@Override
	public long countTaskAll(TaskFilter TaskFilter) {
		return taskRepositoryCustom.countAll(TaskFilter);
	}

	@Override
	public List<Task> findTaskAll(TaskFilter TaskFilter) {
		return taskRepositoryCustom.findAll(TaskFilter).stream().sorted(Comparator.comparingInt(this::getPriorityWeight)).toList();
	}
	
	private int getPriorityWeight(Task task) {
		if(task.getCompleted() == null) {
			if(TaskPriority.hoatoc.getKey().equals(task.getPriority())) {
				return 0;
			}else if(TaskPriority.khan.getKey().equals(task.getPriority())) {
				return 1;
			}else if(task.isRequiredKpi()) {
				return 2;
			}
		}
		return 3;
	}

	@Override
	public Optional<Task> findTaskById(String id) {
		if(ObjectId.isValid(id))
			return taskRepository.findById(new ObjectId(id));
		throw new ObjectIdExceptionAdvance("taskId ["+id+"] không hợp lệ");
	}

	@Override
	public Task getTaskById(String id) {
		Optional<Task> findTask=findTaskById(id);
		if(findTask.isPresent()) {
			return findTask.get();
		}
		throw new NotFoundElementExceptionAdvice("taskId ["+id+"] không tồn tại trong hệ thống");
	}

	@Override
	public Task deleteTaskById(String id) {
		Task task=getTaskById(id);
		taskRepository.delete(task);
		
		/* Đồng bộ countTask cho doc */
		if(task.getDocId()!=null) {
			docService.syncCountTask(task.getDocId());
		}
		
		/* Minus countSubTask for parent task */
		if(task.getParentId()!=null) {
			minusCountSubTask(task.getParentId());
		}
		return task;
	}

	@Override
	public Task saveTask(Task task, TaskAction taskAction, Creator creator) {
		try {
			task = taskRepository.save(task);
			
			/* Save history */
			if(taskAction!=null) {
				saveHistory(task, taskAction.getKey(), creator);
			}
			
			/* Đồng bộ countTask cho doc */
			if(task.getDocId()!=null) {
				docService.syncCountTask(task.getDocId());
			}
			
			return task;
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}

	@Override
	public Task createTask(ReqTaskCreate reqTaskCreate) {
		Task task=new Task();
		task.setCreatedTime(reqTaskCreate.getCreateTime() == 0 ? new Date() : new Date(reqTaskCreate.getCreateTime()));
		task.setUpdatedTime(new Date());
		
		/* Văn bản giao nhiệm vụ */
		if(propsService.isTaskFieldRequiredDoc() && reqTaskCreate.getDocId()==null) {
			throw new BadRequestExceptionAdvice("docId yêu cầu phải bắt buộc");
		}
		
		if(reqTaskCreate.getDocId()!=null) {
			Doc doc=docService.getDocById(reqTaskCreate.getDocId());
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(doc.getNumber());
			taskDocInfo.setSymbol(doc.getSymbol());
			taskDocInfo.setSummary(doc.getSummary());
			taskDocInfo.setCategory(doc.getCategory()!=null?doc.getCategory().getKey():null);
			
			task.setDocId(doc.getId());
			task.setDocInfo(taskDocInfo);
		}
		
		List<TaskSupport> supports = reqTaskCreate.getSupports().stream().map(e->{
			return e.toTaskSupport();
		}).toList();
		
		List<TaskFollower> followers = reqTaskCreate.getFollowers().stream().map(e->{
			return e.toTaskFollower();
		}).toList();
		
		task.setOwner(reqTaskCreate.getOwner().toTaskOwner());
		task.setAssistant(reqTaskCreate.getAssistant().toTaskAssistant());
		task.setAssignee(reqTaskCreate.getAssignee().toTaskAssignee());
		task.setSupports(supports);
		task.setFollowers(followers);
		task.setPriority(reqTaskCreate.getPriority());
		task.setTitle(reqTaskCreate.getTitle());
		task.setDescription(reqTaskCreate.getDescription());
		if(reqTaskCreate.getEndTime()>0) {
			task.setEndTime(new Date(reqTaskCreate.getEndTime()));
		}
		task.setRequiredConfirm(reqTaskCreate.isRequiredConfirm());
		task.setRequiredKpi(reqTaskCreate.isRequiredKpi());
		task.setAttachments(reqTaskCreate.getAttachments());
		task.setState(TaskState.chuathuchien.getKey());
		
		/* Ghi nhật ký */
		task.getEvents().add(0, taskEventService.buildEventCreateTask(reqTaskCreate));
		
		try {
			task = taskRepository.save(task);
			
			/* Save history */
			saveHistory(task, TaskAction.taomoi.getKey(), reqTaskCreate.getCreator().toCreator());
			
			/* Đồng bộ countTask cho doc */
			if(task.getDocId()!=null) {
				docService.syncCountTask(task.getDocId());
			}
			
			return task;
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}
	
	@Override
	public Task createTaskChild(@Valid ReqTaskChildCreate reqTaskChildCreate, String parentTaskId) {
		Task task=new Task();
		task.setCreatedTime(new Date());
		task.setUpdatedTime(new Date());

		/* Nhiệm vụ cha */
		Task taskParent=getTaskById(parentTaskId);
		task.setParentId(taskParent.getId());
		
		/* Văn bản giao nhiệm vụ*/
		if(reqTaskChildCreate.getDocId()!=null) {
			Doc doc=docService.getDocById(reqTaskChildCreate.getDocId());
			
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(doc.getNumber());
			taskDocInfo.setSymbol(doc.getSymbol());
			taskDocInfo.setSummary(doc.getSummary());
			taskDocInfo.setCategory(doc.getCategory()!=null?doc.getCategory().getKey():null);
			
			task.setDocId(doc.getId());
			task.setDocInfo(taskDocInfo);
		}
		
		List<TaskSupport> supports = reqTaskChildCreate.getSupports().stream().map(e->{
			return e.toTaskSupport();
		}).toList();
		
		List<TaskFollower> followers = reqTaskChildCreate.getFollowers().stream().map(e->{
			return e.toTaskFollower();
		}).toList();
		
		task.setOwner(reqTaskChildCreate.getOwner().toTaskOwner());
		task.setAssistant(reqTaskChildCreate.getAssistant().toTaskAssistant());
		task.setAssignee(reqTaskChildCreate.getAssignee().toTaskAssignee());
		task.setSupports(supports);
		task.setFollowers(followers);
		task.setPriority(reqTaskChildCreate.getPriority());
		task.setTitle(reqTaskChildCreate.getTitle());
		task.setDescription(reqTaskChildCreate.getDescription());
		if(reqTaskChildCreate.getEndTime()>0) {
			task.setEndTime(new Date(reqTaskChildCreate.getEndTime()));
		}
		task.setRequiredConfirm(reqTaskChildCreate.isRequiredConfirm());
		if(reqTaskChildCreate.isRequiredConfirm()==false) {
			task.setAllowDelegate(reqTaskChildCreate.isAllowDelegate());
		}else {
			task.setAllowDelegate(false);
		}
		
		task.setRequiredKpi(reqTaskChildCreate.isRequiredKpi());
		
		task.setAttachments(reqTaskChildCreate.getAttachments());
		task.setState(TaskState.chuathuchien.getKey());
		
		/* Ghi nhật ký */
		task.getEvents().add(0, taskEventService.buildEventCreateChildTask(reqTaskChildCreate, taskParent));
		
		try {
			task = taskRepository.save(task);
			
			/* Save history */
			saveHistory(task, TaskAction.taomoi.getKey(), reqTaskChildCreate.getCreator().toCreator());
			
			/* Plus countSubTask for parent task */
			plusCountSubTask(parentTaskId);
			
			return task;
		} catch (Exception e) {
			if(e instanceof DuplicateKeyException) {
				throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
			}
			throw e;
		}
	}

	private void saveHistory(Task task, String action, Creator creator) {
		Thread thread=new Thread(new Runnable() {
			@Override
			public void run() {
				taskHistoryService.saveTaskHistory(task, action, creator);
			}
		});
		thread.start();
	}
	
	/**
	 * Cập nhật nhiệm vụ
	 */
	@Override
	public Task updateTask(String taskId, ReqTaskUpdate reqTaskUpdate) {
		Task task=getTaskById(taskId);
		task.setCreatedTime(reqTaskUpdate.getCreateTime() == 0 ? new Date(): new Date(reqTaskUpdate.getCreateTime()));
		
		/* Văn bản giao nhiệm vụ */
		if(propsService.isTaskFieldRequiredDoc() && reqTaskUpdate.getDocId()==null) {
			throw new BadRequestExceptionAdvice("docId yêu cầu phải bắt buộc");
		}
		
		if(reqTaskUpdate.getDocId()!=null) {
			Doc doc=docService.getDocById(reqTaskUpdate.getDocId());
			TaskDocInfo taskDocInfo=new TaskDocInfo();
			taskDocInfo.setNumber(doc.getNumber());
			taskDocInfo.setSymbol(doc.getSymbol());
			taskDocInfo.setSummary(doc.getSummary());
			taskDocInfo.setCategory(doc.getCategory()!=null?doc.getCategory().getKey():null);
			
			task.setDocId(doc.getId());
			task.setDocInfo(taskDocInfo);
		}
		
		List<TaskSupport> supports = reqTaskUpdate.getSupports().stream().map(e->{
			return e.toTaskSupport();
		}).toList();
		
		List<TaskFollower> followers = reqTaskUpdate.getFollowers().stream().map(e->{
			return e.toTaskFollower();
		}).toList();
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState!=TaskState.dahoanthanh) {
			task.setOwner(reqTaskUpdate.getOwner().toTaskOwner());
			task.setAssistant(reqTaskUpdate.getAssistant().toTaskAssistant());
			task.setAssignee(reqTaskUpdate.getAssignee().toTaskAssignee());
			task.setSupports(supports);
			task.setFollowers(followers);
			task.setPriority(reqTaskUpdate.getPriority());
			task.setTitle(reqTaskUpdate.getTitle());
			task.setDescription(reqTaskUpdate.getDescription());
			if(reqTaskUpdate.getEndTime()>0) {
				task.setEndTime(new Date(reqTaskUpdate.getEndTime()));
			}
			task.setRequiredConfirm(reqTaskUpdate.isRequiredConfirm());
			task.setRequiredKpi(reqTaskUpdate.isRequiredKpi());
			task.setAttachments(reqTaskUpdate.getAttachments());
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventUpdateTask(task, reqTaskUpdate));
			
			try {
				task = taskRepository.save(task);
				
				/* Save history */
				saveHistory(task, TaskAction.capnhat.getKey(), reqTaskUpdate.getCreator().toCreator());
				
				return task;
			} catch (Exception e) {
				if(e instanceof DuplicateKeyException) {
					throw new DuplicateKeyExceptionAdvice("Dữ liệu bị trùng khóa chính, vui lòng thử lại");
				}
				throw e;
			}
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể cập nhật, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	/**
	 * Từ chối thực hiện vì lý do nào đó
	 */
	@Override
	public Task doRefuseTask(String taskId, ReqTaskDoRefuse reqTaskDoRefuse) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		/* Chỉ khi state là chưa thực hiện thì mới từ chối được */
		if(taskState==TaskState.chuathuchien) {
			TaskRefuse taskRefuse = new TaskRefuse();
			taskRefuse.setReasonRefuse(reqTaskDoRefuse.getReasonRefuse());
			taskRefuse.setAttachments(reqTaskDoRefuse.getAttachments());
			taskRefuse.setCreator(reqTaskDoRefuse.getCreator().toCreatorInfo());
			task.setRefuse(taskRefuse);
			
			task.setState(TaskState.tuchoithuchien.getKey());
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventRefuseTask(task, reqTaskDoRefuse));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể từ chối, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	/**
	 * Bắt đầu thực hiện nhiệm vụ
	 */
	@Override
	public Task doAcceptTask(String taskId, ReqTaskDoAccept reqTaskDoAccept) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.chuathuchien || taskState==TaskState.thuchienlai) {
			task.setStartingTime(new Date());
			task.setState(TaskState.dangthuchien.getKey());
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventAcceptTask(task, reqTaskDoAccept));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể bắt đầu thực hiện, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	/**
	 * Hoàn thành nhiệm vụ
	 */
	@Override
	public Task doCompleteTask(String taskId, ReqTaskDoComplete reqTaskDoComplete) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState!=TaskState.dahoanthanh && taskState!=TaskState.tamhoan) {
			if(task.isRequiredConfirm()==false || (reqTaskDoComplete.getIgnoreRequiredConfirm()!=null && reqTaskDoComplete.getIgnoreRequiredConfirm().booleanValue())) {
				if(task.getProcesses().size()>0 && task.getProcesses().getFirst().getPercent()==100) {
					if(task.isCompleted()==false) {
						Date completedTime = new Date(reqTaskDoComplete.getCompletedTime());
						
						if(task.getStartingTimeLong() <= completedTime.getTime() && completedTime.getTime() <= DateTimeUtil.currentTimeMillisFuture()) {
							TaskCompleteStatus taskCompleteStatus=TaskCompleteStatus.khonghan; 
							if(task.hasEndTime()) {
								if(completedTime.getTime() <= task.getEndTime().getTime()) {
									taskCompleteStatus=TaskCompleteStatus.tronghan;
								}else {
									taskCompleteStatus=TaskCompleteStatus.quahan;
								}
							}
							
							TaskCompleted taskCompleted=new TaskCompleted();
							taskCompleted.setCompletedTime(completedTime);
							taskCompleted.setCompletedStatus(taskCompleteStatus.getKey());
							taskCompleted.setAttachments(reqTaskDoComplete.getAttachments());
							taskCompleted.setCreator(reqTaskDoComplete.getCreator().toCreatorInfo());
							task.setCompleted(taskCompleted);
							
							task.setState(TaskState.dahoanthanh.getKey());
							
							/* Ghi nhật ký */
							task.getEvents().add(0, taskEventService.buildEventCompleteTask(task, reqTaskDoComplete));
							
							taskRepository.save(task);
							return task;
						}
						throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể hoàn thành, vì thời gian hoàn thành không hợp lệ");
					}
					throw new NotAcceptableExceptionAdvice("Nhiệm vụ đã hoàn thành trước đó");
				}
				throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể hoàn thành, vì chưa cập nhật tiến độ 100%");
			}
			throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể hoàn thành, vì nhiệm vụ phải qua bước xét duyệt");
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể hoàn thành, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	
	@Override
	public Task doReverseTask(String taskId, ReqTaskDoReverse reqTaskDoReverse) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.choxacnhan || taskState==TaskState.dahoanthanh) {
			TaskReverse taskReverse=new TaskReverse();
			taskReverse.setReasonReverse(reqTaskDoReverse.getReasonReverse());
			taskReverse.setAttachments(reqTaskDoReverse.getAttachments());
			taskReverse.setCreator(reqTaskDoReverse.getCreator().toCreatorInfo());
			task.setReverse(taskReverse);
			task.getReverseHistories().add(0, taskReverse);
			
			task.setState(TaskState.dangthuchien.getKey());
			task.setReported(null);
			task.setCompleted(null);
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventReverseCompleteTask(task, reqTaskDoReverse));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể triệu hồi, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doRedoTask(String taskId, @Valid ReqTaskDoRedo reqTaskDoRedo) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.choxacnhan || taskState==TaskState.tuchoixacnhan || taskState==TaskState.dahoanthanh) {
			TaskRedo taskRedo=new TaskRedo();
			taskRedo.setReasonRedo(reqTaskDoRedo.getReasonRedo());
			taskRedo.setAttachments(reqTaskDoRedo.getAttachments());
			taskRedo.setCreator(reqTaskDoRedo.getCreator().toCreatorInfo());
			task.setRedo(taskRedo);
			task.getRedoHistories().add(0, taskRedo);
			
			task.setState(TaskState.thuchienlai.getKey());
			task.setReported(null);
			task.setCompleted(null);
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventRedoTask(task, reqTaskDoRedo));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể yêu cầu thực hiện lại, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doReportTask(String taskId, @Valid ReqTaskDoReport reqTaskDoReport) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState!=TaskState.dahoanthanh && taskState!=TaskState.tamhoan) {
			if(task.isRequiredConfirm()) {
				if(task.getProcesses().size()>0 && task.getProcesses().getFirst().getPercent()==100) {
					if(task.isReported()==false) {
						Date completedTime = new Date(reqTaskDoReport.getCompletedTime());
						System.out.println("starting: "+task.getStartingTimeLong());
						System.out.println("completed: "+completedTime.getTime());
						System.out.println("current: "+DateTimeUtil.currentTimeMillisFuture());
						
						if(task.getStartingTimeLong() <= completedTime.getTime() && completedTime.getTime() <= DateTimeUtil.currentTimeMillisFuture()) {
							TaskCompleteStatus taskCompleteStatus=TaskCompleteStatus.khonghan; 
							if(task.hasEndTime()) {
								if(completedTime.getTime() <= task.getEndTime().getTime()) {
									taskCompleteStatus=TaskCompleteStatus.tronghan;
								}else {
									taskCompleteStatus=TaskCompleteStatus.quahan;
								}
							}
							
							TaskReported taskReported=new TaskReported();
							taskReported.setCompletedTime(completedTime);
							taskReported.setReportedStatus(taskCompleteStatus.getKey());
							taskReported.setAttachments(reqTaskDoReport.getAttachments());
							taskReported.setCreator(reqTaskDoReport.getCreator().toCreatorInfo());
							task.setReported(taskReported);
							
							task.setState(TaskState.choxacnhan.getKey());
							
							taskRepository.save(task);
							return task;
						}
						throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể báo cáo, vì thời gian hoàn thành không hợp lệ");
					}
					throw new NotAcceptableExceptionAdvice("Nhiệm vụ đã báo cáo trước đó");
				}
				throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể báo cáo, vì chưa cập nhật tiến độ 100%");
			} 
			throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể báo cáo, vì nhiệm vụ không yêu cầu báo cáo xét duyệt");
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể báo cáo, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doConfirmTask(String taskId, ReqTaskDoConfirm reqTaskDoConfirm) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.choxacnhan) {
			if(task.isRequiredConfirm()) {
				if(task.isReported()) {
					TaskReported reported=task.getReported();
					if(task.isCompleted()==false) {
						Date completedTime = new Date(reqTaskDoConfirm.getCompletedTime());
						//if(task.getStartingTimeLong() <= completedTime.getTime() && completedTime.getTime() <= new Date().getTime()) {
							TaskCompleteStatus taskCompleteStatus=TaskCompleteStatus.khonghan; 
							if(task.hasEndTime()) {
								if(completedTime.getTime() <= task.getEndTime().getTime()) {
									taskCompleteStatus=TaskCompleteStatus.tronghan;
								}else {
									taskCompleteStatus=TaskCompleteStatus.quahan;
								}
							}
							
							TaskCompleted taskCompleted=new TaskCompleted();
							taskCompleted.setConfirmedTime(new Date());
							taskCompleted.setCompletedTime(completedTime);
							taskCompleted.setCompletedStatus(taskCompleteStatus.getKey());
							taskCompleted.setAttachments(reported.getAttachments());
							taskCompleted.setCreator(reqTaskDoConfirm.getCreator().toCreatorInfo());
							task.setCompleted(taskCompleted);
							
							task.setState(TaskState.dahoanthanh.getKey());
							
							/* Ghi nhật ký */
							task.getEvents().add(0, taskEventService.buildEventConfirmTask(task, reqTaskDoConfirm));
							
							taskRepository.save(task);
							return task;
						//}
						//throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể xác nhận hoàn thành, vì thời gian hoàn thành không hợp lệ");
					}
					throw new NotAcceptableExceptionAdvice("Nhiệm vụ đã hoàn thành, không thể xác nhận hoàn thành");
				}
				throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể xác nhận hoàn thành, vì chưa báo cáo");
			}
			throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể xác nhận hoàn thành, vì không yêu cầu xét duyệt");
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể xác nhận hoàn thành, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doConfirmRefuseTask(String taskId, @Valid ReqTaskDoConfirmRefuse reqTaskDoConfirmRefuse) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.choxacnhan) {
			if(task.isRequiredConfirm()) {
				if(task.isReported()) {
					if(task.isCompleted()==false) {
						if(task.getConfirmRefuse()==null) {
							TaskConfirmRefuse taskConfirmRefuse=new TaskConfirmRefuse();
							taskConfirmRefuse.setCreatedTime(new Date());
							taskConfirmRefuse.setReasonConfirmRefuse(reqTaskDoConfirmRefuse.getReasonConfirmRefuse());
							taskConfirmRefuse.setAttachments(reqTaskDoConfirmRefuse.getAttachments());
							taskConfirmRefuse.setCreator(reqTaskDoConfirmRefuse.getCreator().toCreatorInfo());
							
							task.setConfirmRefuse(taskConfirmRefuse);
							
							/* Cập nhật reported vào histories và set lại là null */
							if(task.getReported()!=null) {
								TaskReported taskReported=task.getReported();
								task.getReportedHistories().add(taskReported);
							}
							task.setReported(null);
							task.setCompleted(null);
							
							/* Chuyển sang state từ chối xác nhận */
							task.setState(TaskState.tuchoixacnhan.getKey());
							
							/* Ghi nhật ký */
							task.getEvents().add(0, taskEventService.buildEventConfirmRefuseTask(task, reqTaskDoConfirmRefuse));
							
							taskRepository.save(task);
							return task;
						}
						throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể từ chối xác nhận, vì đã từ chối rồi");
					}
					throw new NotAcceptableExceptionAdvice("Nhiệm vụ đã hoàn thành, không thể từ chối xác nhận");
				}
				throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể từ chối xác nhận, vì chưa báo cáo");
			}
			throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể từ chối xác nhận, vì không yêu cầu xét duyệt");
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể từ chối xác nhận, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doRedoAndReportAgainTask(String taskId, ReqTaskDoRedoAndReportAgain reqTaskDoRedoAndReportAgain) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.tuchoixacnhan) {
			task.setStartingTimeAgain(new Date());
			task.setReported(null);
			task.setState(TaskState.dangthuchien.getKey());
			
			/* Gán lại kết quả từ chối trước đó thành null */
			if(task.getConfirmRefuse()!=null) {
				task.getConfirmRefuseHistories().add(task.getConfirmRefuse());
			}
			task.setConfirmRefuse(null);
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventRedoAndReportAgainTask(task, reqTaskDoRedoAndReportAgain));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể thực hiện và báo cáo lại, vì trạng thái đang là ["+taskState.getName()+"]");
	}
	
	@Override
	public Task doPendingTask(String taskId, ReqTaskDoPending reqTaskDoPending) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState!=TaskState.dahoanthanh) {
			TaskPending taskPending=new TaskPending();
			taskPending.setReasonPending(reqTaskDoPending.getReasonPending());
			taskPending.setAttachments(reqTaskDoPending.getAttachments());
			taskPending.setCreator(reqTaskDoPending.getCreator().toCreatorInfo());
			task.setPending(taskPending);
			
			String statusCurrent=task.getState();
			task.setState(TaskState.tamhoan.getKey());
			task.setStatePrevious(statusCurrent);
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventPendingTask(task, reqTaskDoPending));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể tạm hoãn, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doUnPendingTask(String taskId, ReqTaskDoUnPending reqTaskDoUnPending) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.tamhoan) {
			String statusCurrent=task.getState();
			task.setState(task.getStatePrevious());
			task.setStatePrevious(statusCurrent);
			task.getPendingHistories().add(0, task.getPending());
			task.setPending(null);
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventUnPendingTask(task, reqTaskDoUnPending));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể tiếp tục, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	
	@Override
	public Task doRatingTask(String taskId, ReqTaskDoRating reqTaskDoRating) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.dahoanthanh) {
			TaskRating taskRating=new TaskRating();
			taskRating.setStar(reqTaskDoRating.getStar());
			taskRating.setExplain(reqTaskDoRating.getExplain());
			taskRating.setCreator(reqTaskDoRating.getCreator().toCreatorInfo());
			taskRating.setMaskA(reqTaskDoRating.getMarkA());
			taskRating.setMaskB(reqTaskDoRating.getMarkB());
			taskRating.setMaskC(reqTaskDoRating.getMarkC());
			if(task.isRequiredKpi()) {
				Double total = (reqTaskDoRating.getMarkA() + reqTaskDoRating.getMarkB() + reqTaskDoRating.getMarkC()) / 3;
				taskRating.setTotalPercent(total);
				double mark = ((total * 70) / 100) + 30;
				taskRating.setTotalMark(mark);
			}
			
			task.setRating(taskRating);
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventRatingTask(task, reqTaskDoRating));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể đánh giá, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doUpdateProcessTask(String taskId, @Valid ReqTaskDoUpdateProcess reqTaskDoUpdateProcess) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState!=TaskState.dahoanthanh) {
			TaskProcess taskProcess=new TaskProcess();
			taskProcess.setPercent(reqTaskDoUpdateProcess.getPercent());
			taskProcess.setExplain(reqTaskDoUpdateProcess.getExplain());
			taskProcess.setAttachments(reqTaskDoUpdateProcess.getAttachments());
			taskProcess.setCreator(reqTaskDoUpdateProcess.getCreator().toCreatorInfo());
			task.getProcesses().add(0, taskProcess);
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventUpdateProcessTask(task, reqTaskDoUpdateProcess));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể cập nhật tiến độ, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doRemindTask(String taskId, @Valid ReqTaskDoRemind reqTaskDoRemind) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState!=TaskState.dahoanthanh) {
			TaskRemind taskRemind=new TaskRemind();
			taskRemind.setReasonRemind(reqTaskDoRemind.getReasonRemind());
			taskRemind.setAttachments(reqTaskDoRemind.getAttachments());
			taskRemind.setCreator(reqTaskDoRemind.getCreator().toCreatorInfo());
			task.getReminds().add(0, taskRemind);
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventRemindTask(task, reqTaskDoRemind));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể nhắc nhở, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doCommentTask(String taskId, @Valid ReqTaskDoComment reqTaskDoComment) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState!=TaskState.dahoanthanh) {
			TaskComment taskComment=new TaskComment();
			taskComment.setMessage(reqTaskDoComment.getMessage());
			taskComment.setAttachments(reqTaskDoComment.getAttachments());
			taskComment.setCreator(reqTaskDoComment.getCreator().toCreatorInfo());
			task.getComments().add(0, taskComment);
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventCommentTask(task, reqTaskDoComment));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể trao đổi, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doReplyCommentTask(String taskId, String parentCommentId, @Valid ReqTaskDoComment reqTaskDoComment) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState!=TaskState.dahoanthanh) {
			TaskComment taskComment=new TaskComment();
			taskComment.setMessage(reqTaskDoComment.getMessage());
			taskComment.setAttachments(reqTaskDoComment.getAttachments());
			taskComment.setCreator(reqTaskDoComment.getCreator().toCreatorInfo());
			
			/* Kiểm tra parentCommentId và setReplies() */
			boolean flag=false;
			for(TaskComment parentComment:task.getComments()) {
				if(parentComment.getId().equals(parentCommentId)) {
					parentComment.getReplies().add(taskComment);
					flag=true;
					break;
				}else if(parentComment.getReplies().size()>0){
					for(TaskComment childComment:parentComment.getReplies()) {
						if(childComment.getId().equals(parentCommentId)) {
							parentComment.getReplies().add(taskComment);
							flag=true;
							break;
						}
					}
					
					if(flag) {
						break;
					}
				}
			}
			
			if(!flag) {
				throw new NotAcceptableExceptionAdvice("Ý kiến trả lời phản hồi không tìm thấy");
			}
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventReplyCommentTask(task, parentCommentId, reqTaskDoComment));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không trao đổi, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doAssignUserAssignee(String taskId, String organizationId, @Valid ReqTaskDoAssignUserAssignee reqTaskDoAssignUserAssignee) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.chuathuchien || taskState==TaskState.dangthuchien || taskState==TaskState.tuchoixacnhan) {
			TaskAssignee assignee=null;
			if(task.getAssignee().getOrganizationId().equals(organizationId)) {
				User userAssignee=userService.getUserById(reqTaskDoAssignUserAssignee.getOrganizationUserId());
				task.getAssignee().setOrganizationUserId(userAssignee.getId());
				task.getAssignee().setOrganizationUserName(userAssignee.getFullName());
				assignee=task.getAssignee();
			}else {
				throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể gán cán bộ xử lý, vì tổ chức không hợp lệ");
			}
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventAssignUserAssigneeTask(task, assignee, reqTaskDoAssignUserAssignee));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể gán cán bộ xử lý, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doUnAssignUserAssignee(String taskId, String organizationId, @Valid ReqTaskDoUnAssignUserAssignee reqTaskDoUnAssignUserAssignee) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.chuathuchien || taskState==TaskState.dangthuchien || taskState==TaskState.tuchoixacnhan) {
			TaskAssignee assignee=null;
			if(task.getAssignee().getOrganizationId().equals(organizationId)) {
				if(task.getAssignee().getOrganizationUserId()==null) {
					throw new NotAcceptableExceptionAdvice("Nhiệm vụ đã được hủy gán cán bộ trước đó");
				}
				assignee=task.getAssignee();
				task.getAssignee().setOrganizationUserId(null);
				task.getAssignee().setOrganizationUserName(null);
			}else {
				throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể hủy gán cán bộ xử lý, vì tổ chức không hợp lệ");
			}
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventUnAssignUserAssigneeTask(task, assignee, reqTaskDoUnAssignUserAssignee));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể hủy gán cán bộ xử lý, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doAssignUserSupport(String taskId, String organizationId, @Valid ReqTaskDoAssignUserSupport reqTaskDoAssignUserSupport) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.chuathuchien || taskState==TaskState.dangthuchien || taskState==TaskState.tuchoixacnhan) {
			boolean exists=false;
			TaskSupport support=null;
			User userAssignee=userService.getUserById(reqTaskDoAssignUserSupport.getOrganizationUserId());
			for(TaskSupport item:task.getSupports()) {
				if(item.getOrganizationId().equals(organizationId)) {
					item.setOrganizationUserId(userAssignee.getId());
					item.setOrganizationUserName(userAssignee.getFullName());
					support=item;
					exists=true;
					break;
				}
			}
				
			if(!exists){
				throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể gán cán bộ xử lý, vì tổ chức không hợp lệ");
			}
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventAssignUserSupportTask(task, support, reqTaskDoAssignUserSupport));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể gán cán bộ xử lý, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public Task doUnAssignUserSupport(String taskId, String organizationId, @Valid ReqTaskDoUnAssignUserSupport reqTaskDoUnAssignUserSupport) {
		Task task=getTaskById(taskId);
		
		TaskState taskState=EnumUtils.getEnumIgnoreCase(TaskState.class, task.getState());
		if(taskState==TaskState.chuathuchien || taskState==TaskState.dangthuchien || taskState==TaskState.tuchoixacnhan) {
			boolean exists=false;
			TaskSupport support=null;
			for(TaskSupport item:task.getSupports()) {
				if(item.getOrganizationId().equals(organizationId)) {
					if(item.getOrganizationUserId()==null) {
						throw new NotAcceptableExceptionAdvice("Nhiệm vụ đã được hủy gán cán bộ trước đó");
					}
					support=item;
					item.setOrganizationUserId(null);
					item.setOrganizationUserName(null);
					exists=true;
					break;
				}
			}
				
			if(!exists){
				throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể hủy gán cán bộ xử lý, vì tổ chức không hợp lệ");
			}
			
			/* Ghi nhật ký */
			task.getEvents().add(0, taskEventService.buildEventUnAssignUserSupportTask(task, support, reqTaskDoUnAssignUserSupport));
			
			taskRepository.save(task);
			return task;
		}
		throw new NotAcceptableExceptionAdvice("Nhiệm vụ không thể hủy gán cán bộ xử lý, vì trạng thái đang là ["+taskState.getName()+"]");
	}

	@Override
	public long plusCountSubTask(String taskId) {
		Task task=getTaskById(taskId);
		int currrent=(int) getCountSubTask(taskId);
		task.setCountSubTask(currrent);
		saveTask(task, null, null);
		return task.getCountSubTask();
	}

	@Override
	public long minusCountSubTask(String taskId) {
		Task task=getTaskById(taskId);
		int currrent=(int) getCountSubTask(taskId);
		if(currrent>0) {
			task.setCountSubTask(currrent);
			saveTask(task, null, null);
		}
		return task.getCountSubTask();
	}

	@Override
	public long getCountTaskOfDoc(String docId) {
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.getDocIds().add(docId);
		taskFilter.setTaskRoot(true);
		return taskRepositoryCustom.countAll(taskFilter);
	}

	@Override
	public long getCountSubTask(String taskId) {
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setParentIds(Arrays.asList(taskId));
		return taskRepositoryCustom.countAll(taskFilter);
	}

	@Override
	public boolean isReferenceAnyTask(String organizationId, String userId) {
		TaskUserRefFilter taskUserRefFilter=new TaskUserRefFilter();
		taskUserRefFilter.setOrganizationId(organizationId);
		taskUserRefFilter.setOrganizationUserId(userId);
		
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setFindUserRefFilter(taskUserRefFilter);
		
		return taskRepositoryCustom.findOne(taskFilter).isPresent();
	}

}

package ws.core.service.impl;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import ws.core.enums.TaskEventAction;
import ws.core.enums.TaskPriority;
import ws.core.model.Task;
import ws.core.model.embeded.TaskAssignee;
import ws.core.model.embeded.TaskEvent;
import ws.core.model.embeded.TaskEventItemDescription;
import ws.core.model.embeded.TaskSupport;
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
import ws.core.model.request.ReqTaskDoReverse;
import ws.core.model.request.ReqTaskDoUnAssignUserAssignee;
import ws.core.model.request.ReqTaskDoUnAssignUserSupport;
import ws.core.model.request.ReqTaskDoUnPending;
import ws.core.model.request.ReqTaskDoUpdateProcess;
import ws.core.model.request.ReqTaskUpdate;
import ws.core.services.TaskEventService;
import ws.core.util.DateTimeUtil;

@Service
public class TaskEventServiceImpl implements TaskEventService{

	@Override
	public TaskEvent buildEventCreateTask(ReqTaskCreate reqTaskCreate) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskCreate.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.taonhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.taonhiemvu.getName());
		taskEvent.setTitle("Tạo nhiệm vụ");
		
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Tiêu đề", reqTaskCreate.getTitle()));
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Nội dung", reqTaskCreate.getDescription()));
		
		if(EnumUtils.isValidEnum(TaskPriority.class, reqTaskCreate.getPriority())) {
			taskEvent.getDescriptions().add(new TaskEventItemDescription("Độ khẩn", EnumUtils.getEnum(TaskPriority.class, reqTaskCreate.getPriority()).getName()));
		}
		
		if(reqTaskCreate.getEndTime()>0) {
			taskEvent.getDescriptions().add(new TaskEventItemDescription("Hạn xử lý", DateTimeUtil.getDatetimeFormat().format(reqTaskCreate.getEndTime())));
		}else {
			taskEvent.getDescriptions().add(new TaskEventItemDescription("Hạn xử lý", "Không hạn"));
		}
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventUpdateTask(Task task, ReqTaskUpdate reqTaskUpdate) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskUpdate.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.capnhatnhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.capnhatnhiemvu.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Tiêu đề", reqTaskUpdate.getTitle()));
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Nội dung", reqTaskUpdate.getDescription()));
		
		if(EnumUtils.isValidEnum(TaskPriority.class, reqTaskUpdate.getPriority())) {
			taskEvent.getDescriptions().add(new TaskEventItemDescription("Độ khẩn", EnumUtils.getEnum(TaskPriority.class, reqTaskUpdate.getPriority()).getName()));
		}
		
		if(reqTaskUpdate.getEndTime()>0) {
			taskEvent.getDescriptions().add(new TaskEventItemDescription("Hạn xử lý", DateTimeUtil.getDatetimeFormat().format(reqTaskUpdate.getEndTime())));
		}else {
			taskEvent.getDescriptions().add(new TaskEventItemDescription("Hạn xử lý", "Không hạn"));
		}
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventRefuseTask(Task task, ReqTaskDoRefuse reqTaskDoReturn) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoReturn.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.tuchoithuchien.getKey());
		taskEvent.setTitle(TaskEventAction.tuchoithuchien.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Lý do", reqTaskDoReturn.getReasonRefuse()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventAcceptTask(Task task, ReqTaskDoAccept reqTaskDoAccept) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoAccept.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.thuchiennhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.thuchiennhiemvu.getName());
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventCompleteTask(Task task, ReqTaskDoComplete reqTaskDoComplete) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoComplete.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.hoanthanhnhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.hoanthanhnhiemvu.getName());
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventReverseCompleteTask(Task task, ReqTaskDoReverse reqTaskDoReverseComplete) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoReverseComplete.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.trieuhoinhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.trieuhoinhiemvu.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Lý do", reqTaskDoReverseComplete.getReasonReverse()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventConfirmTask(Task task, ReqTaskDoConfirm reqTaskDoConfirm) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoConfirm.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.xacnhanhoanthanh.getKey());
		taskEvent.setTitle(TaskEventAction.xacnhanhoanthanh.getName());
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventConfirmRefuseTask(Task task, @Valid ReqTaskDoConfirmRefuse reqTaskDoConfirmRefuse) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoConfirmRefuse.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.tuchoixacnhan.getKey());
		taskEvent.setTitle(TaskEventAction.tuchoixacnhan.getName());
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventRedoAndReportAgainTask(Task task, ReqTaskDoRedoAndReportAgain reqTaskDoRedoAndReportAgain) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoRedoAndReportAgain.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.thuchienvabaocaolai.getKey());
		taskEvent.setTitle(TaskEventAction.thuchienvabaocaolai.getName());
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventPendingTask(Task task, ReqTaskDoPending reqTaskDoPending) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoPending.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.tamhoanthuchien.getKey());
		taskEvent.setTitle(TaskEventAction.tamhoanthuchien.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Lý do", reqTaskDoPending.getReasonPending()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventUnPendingTask(Task task, ReqTaskDoUnPending reqTaskDoUnPending) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoUnPending.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.tieptucthuchien.getKey());
		taskEvent.setTitle(TaskEventAction.tieptucthuchien.getName());
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventRedoTask(Task task, ReqTaskDoRedo reqTaskDoRedo) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoRedo.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.thuchienlainhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.thuchienlainhiemvu.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Lý do", reqTaskDoRedo.getReasonRedo()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventRatingTask(Task task, ReqTaskDoRating reqTaskDoRating) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoRating.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.danhgianhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.danhgianhiemvu.getName());
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventUpdateProcessTask(Task task, @Valid ReqTaskDoUpdateProcess reqTaskDoUpdateProcess) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoUpdateProcess.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.capnhattiendonhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.capnhattiendonhiemvu.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Phần trăm", reqTaskDoUpdateProcess.getPercent()+""));
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Diễn giải", reqTaskDoUpdateProcess.getExplain()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventRemindTask(Task task, @Valid ReqTaskDoRemind reqTaskDoRemind) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoRemind.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.nhacnhothuchiennhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.nhacnhothuchiennhiemvu.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Nội dung", reqTaskDoRemind.getReasonRemind()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventCommentTask(Task task, @Valid ReqTaskDoComment reqTaskDoComment) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoComment.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.traodoiykiennhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.traodoiykiennhiemvu.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Nội dung", reqTaskDoComment.getMessage()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventReplyCommentTask(Task task, String parentCommentId, @Valid ReqTaskDoComment reqTaskDoComment) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoComment.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.traloitraodoiykiennhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.traloitraodoiykiennhiemvu.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Nội dung", reqTaskDoComment.getMessage()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventCreateChildTask(@Valid ReqTaskChildCreate reqTaskChildCreate, Task taskParent) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskChildCreate.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.taonhiemvu.getKey());
		taskEvent.setTitle(TaskEventAction.taonhiemvu.getName());
		taskEvent.setTitle("Tạo nhiệm vụ");
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Tiêu đề", reqTaskChildCreate.getTitle()));
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Nội dung", reqTaskChildCreate.getDescription()));
		
		if(EnumUtils.isValidEnum(TaskPriority.class, reqTaskChildCreate.getPriority())) {
			taskEvent.getDescriptions().add(new TaskEventItemDescription("Độ khẩn", EnumUtils.getEnum(TaskPriority.class, reqTaskChildCreate.getPriority()).getName()));
		}
		
		if(reqTaskChildCreate.getEndTime()>0) {
			taskEvent.getDescriptions().add(new TaskEventItemDescription("Hạn xử lý", DateTimeUtil.getDatetimeFormat().format(reqTaskChildCreate.getEndTime())));
		}else {
			taskEvent.getDescriptions().add(new TaskEventItemDescription("Hạn xử lý", "Không hạn"));
		}
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventAssignUserAssigneeTask(Task task, TaskAssignee assignee,  @Valid ReqTaskDoAssignUserAssignee reqTaskDoAssignUserAssignee) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoAssignUserAssignee.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.donvixulyphancanboxuly.getKey());
		taskEvent.setTitle(TaskEventAction.donvixulyphancanboxuly.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Đơn vị xử lý", assignee.getOrganizationName()));
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Phân cán bộ xử lý cho", assignee.getOrganizationUserName()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventUnAssignUserAssigneeTask(Task task, TaskAssignee assignee, @Valid ReqTaskDoUnAssignUserAssignee reqTaskDoUnAssignUserAssignee) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoUnAssignUserAssignee.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.donvixulyhuyphancanboxuly.getKey());
		taskEvent.setTitle(TaskEventAction.donvixulyhuyphancanboxuly.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Đơn vị xử lý", assignee.getOrganizationName()));
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Hủy phân cán bộ xử lý cho", assignee.getOrganizationUserName()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventAssignUserSupportTask(Task task, TaskSupport support, @Valid ReqTaskDoAssignUserSupport reqTaskDoAssignUserSupport) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoAssignUserSupport.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.donvihotrophancanbohotro.getKey());
		taskEvent.setTitle(TaskEventAction.donvihotrophancanbohotro.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Đơn vị hỗ trợ", support.getOrganizationName()));
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Phân cán bộ hỗ trợ cho", support.getOrganizationUserName()));
		return taskEvent;
	}

	@Override
	public TaskEvent buildEventUnAssignUserSupportTask(Task task, TaskSupport support, @Valid ReqTaskDoUnAssignUserSupport reqTaskDoUnAssignUserSupport) {
		TaskEvent taskEvent=new TaskEvent();
		taskEvent.setCreator(reqTaskDoUnAssignUserSupport.getCreator().toCreatorInfo());
		taskEvent.setAction(TaskEventAction.donvihotrohuyphancanbohotro.getKey());
		taskEvent.setTitle(TaskEventAction.donvihotrohuyphancanbohotro.getName());
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Đơn vị xử lý", support.getOrganizationName()));
		taskEvent.getDescriptions().add(new TaskEventItemDescription("Hủy phân cán bộ xử lý cho", support.getOrganizationUserName()));
		return taskEvent;
	}

	
}

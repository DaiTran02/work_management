package ws.core.services;

import jakarta.validation.Valid;
import ws.core.model.Task;
import ws.core.model.embeded.TaskAssignee;
import ws.core.model.embeded.TaskEvent;
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

public interface TaskEventService {
	/**
	 * Tạo thông tin event tạo mới nhiệm vụ
	 * @param reqTaskCreate
	 * @return
	 */
	public TaskEvent buildEventCreateTask(@Valid ReqTaskCreate reqTaskCreate);
	
	/**
	 * Tạo thông tin event cập nhật nhiệm vụ
	 * @param reqTaskUpdate
	 * @return
	 */
	public TaskEvent buildEventUpdateTask(Task task, @Valid ReqTaskUpdate reqTaskUpdate);
	
	public TaskEvent buildEventRefuseTask(Task task, @Valid ReqTaskDoRefuse reqTaskDoReturn);
	
	public TaskEvent buildEventAcceptTask(Task task, @Valid ReqTaskDoAccept reqTaskDoAccept);
	
	public TaskEvent buildEventCompleteTask(Task task, @Valid ReqTaskDoComplete reqTaskDoComplete);
	
	public TaskEvent buildEventReverseCompleteTask(Task task, @Valid ReqTaskDoReverse reqTaskDoReverseComplete);
	
	public TaskEvent buildEventConfirmTask(Task task, @Valid ReqTaskDoConfirm reqTaskDoConfirm);
	
	public TaskEvent buildEventPendingTask(Task task, @Valid ReqTaskDoPending reqTaskDoPending);
	
	public TaskEvent buildEventUnPendingTask(Task task, @Valid ReqTaskDoUnPending reqTaskDoUnPending);
	
	public TaskEvent buildEventRedoTask(Task task, @Valid ReqTaskDoRedo reqTaskDoRedo);
	
	public TaskEvent buildEventRatingTask(Task task, @Valid ReqTaskDoRating reqTaskDoRating);

	public TaskEvent buildEventUpdateProcessTask(Task task, @Valid ReqTaskDoUpdateProcess reqTaskDoUpdateProcess);

	public TaskEvent buildEventRemindTask(Task task, @Valid ReqTaskDoRemind reqTaskDoRemind);

	public TaskEvent buildEventCommentTask(Task task, @Valid ReqTaskDoComment reqTaskDoComment);

	public TaskEvent buildEventReplyCommentTask(Task task, String parentCommentId, @Valid ReqTaskDoComment reqTaskDoComment);

	public TaskEvent buildEventCreateChildTask(@Valid ReqTaskChildCreate reqTaskChildCreate, Task taskParent);

	public TaskEvent buildEventAssignUserAssigneeTask(Task task, TaskAssignee assignee, @Valid ReqTaskDoAssignUserAssignee reqTaskDoAssignUserAssignee);

	public TaskEvent buildEventUnAssignUserAssigneeTask(Task task, TaskAssignee assignee, @Valid ReqTaskDoUnAssignUserAssignee reqTaskDoUnAssignUserAssignee);

	public TaskEvent buildEventAssignUserSupportTask(Task task, TaskSupport support, @Valid ReqTaskDoAssignUserSupport reqTaskDoAssignUserSupport);

	public TaskEvent buildEventUnAssignUserSupportTask(Task task, TaskSupport support, @Valid ReqTaskDoUnAssignUserSupport reqTaskDoUnAssignUserSupport);

	public TaskEvent buildEventConfirmRefuseTask(Task task, @Valid ReqTaskDoConfirmRefuse reqTaskDoConfirmRefuse);

	public TaskEvent buildEventRedoAndReportAgainTask(Task task, ReqTaskDoRedoAndReportAgain reqTaskDoRedoAndReportAgain);
}

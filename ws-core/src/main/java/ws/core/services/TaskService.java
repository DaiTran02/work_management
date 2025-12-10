package ws.core.services;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import ws.core.enums.TaskAction;
import ws.core.model.Task;
import ws.core.model.embeded.Creator;
import ws.core.model.filter.TaskFilter;
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

/**
 * The Interface TaskService.
 */
public interface TaskService {
	
	/**
	 * Count task all.
	 *
	 * @param TaskFilter the task filter
	 * @return the long
	 */
	public long countTaskAll(TaskFilter TaskFilter);
	
	/**
	 * Find task all.
	 *
	 * @param TaskFilter the task filter
	 * @return the list
	 */
	public List<Task> findTaskAll(TaskFilter TaskFilter);
	
	/**
	 * Find task by id.
	 *
	 * @param id the id
	 * @return the optional
	 */
	public Optional<Task> findTaskById(String id);
	
	/**
	 * Gets the task by id.
	 *
	 * @param id the id
	 * @return the task by id
	 */
	public Task getTaskById(String id);
	
	/**
	 * Delete task by id.
	 *
	 * @param id the id
	 * @return the task
	 */
	public Task deleteTaskById(String id);
	
	/**
	 * Save task.
	 *
	 * @param task the task
	 * @return the task
	 */
	public Task saveTask(Task task, TaskAction taskAction, Creator creator);
	
	/**
	 * Creates the task.
	 *
	 * @param reqTaskCreate the req task create
	 * @param creator the creator
	 * @return the task
	 */
	public Task createTask(@Valid ReqTaskCreate reqTaskCreate);
	
	/**
	 * Creates the task child.
	 *
	 * @param reqTaskChildCreate the req task child create
	 * @param user the user
	 * @param parentTaskId the parent task id
	 * @return the task
	 */
	public Task createTaskChild(@Valid ReqTaskChildCreate reqTaskChildCreate, String parentTaskId);
	
	/**
	 * Update task.
	 *
	 * @param taskId the task id
	 * @param reqTaskUpdate the req task update
	 * @return the task
	 */
	public Task updateTask(String taskId, @Valid ReqTaskUpdate reqTaskUpdate);
	
	/**
	 * Do refuse task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoRefuse the req task do refuse
	 * @return the task
	 */
	public Task doRefuseTask(String taskId, @Valid ReqTaskDoRefuse reqTaskDoRefuse);
	
	/**
	 * Do accept task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoAccept the req task do accept
	 * @return the task
	 */
	public Task doAcceptTask(String taskId, @Valid ReqTaskDoAccept reqTaskDoAccept);
	
	/**
	 * Do complete task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoComplete the req task do complete
	 * @return the task
	 */
	public Task doCompleteTask(String taskId, @Valid ReqTaskDoComplete reqTaskDoComplete);
	
	/**
	 * Do reverse task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoReverseComplete the req task do reverse complete
	 * @return the task
	 */
	public Task doReverseTask(String taskId, @Valid ReqTaskDoReverse reqTaskDoReverseComplete);
	
	/**
	 * Do report task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoReport the req task do report
	 * @return the task
	 */
	public Task doReportTask(String taskId, @Valid ReqTaskDoReport reqTaskDoReport);
	
	/**
	 * Do confirm task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoConfirm the req task do confirm
	 * @return the task
	 */
	public Task doConfirmTask(String taskId, @Valid ReqTaskDoConfirm reqTaskDoConfirm);
	
	/**
	 * Do confirm refuse task.
	 * @param id
	 * @param reqTaskDoConfirmRefuse
	 * @return
	 */
	public Task doConfirmRefuseTask(String id, @Valid ReqTaskDoConfirmRefuse reqTaskDoConfirmRefuse);

	/**
	 * Thực hiện lại và báo cáo lại, sau khi bị từ chối xác nhận hoàn thành
	 * @param taskId
	 * @param reqTaskDoRedoAndReportAgain
	 * @return
	 */
	public Task doRedoAndReportAgainTask(String taskId, ReqTaskDoRedoAndReportAgain reqTaskDoRedoAndReportAgain);

	/**
	 * Do pending task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoPending the req task do pending
	 * @return the task
	 */
	public Task doPendingTask(String taskId, @Valid ReqTaskDoPending reqTaskDoPending);
	
	/**
	 * Do un pending task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoUnPending the req task do un pending
	 * @return the task
	 */
	public Task doUnPendingTask(String taskId, @Valid ReqTaskDoUnPending reqTaskDoUnPending);
	
	/**
	 * Do rating task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoRating the req task do rating
	 * @return the task
	 */
	public Task doRatingTask(String taskId, @Valid ReqTaskDoRating reqTaskDoRating);

	/**
	 * Do redo task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoRedo the req task do redo
	 * @return the task
	 */
	public Task doRedoTask(String taskId, @Valid ReqTaskDoRedo reqTaskDoRedo);

	/**
	 * Do update process task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoUpdateProcess the req task do update process
	 * @return the task
	 */
	public Task doUpdateProcessTask(String taskId, @Valid ReqTaskDoUpdateProcess reqTaskDoUpdateProcess);

	/**
	 * Do remind task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoRemind the req task do remind
	 * @return the task
	 */
	public Task doRemindTask(String taskId, @Valid ReqTaskDoRemind reqTaskDoRemind);

	/**
	 * Do comment task.
	 *
	 * @param taskId the task id
	 * @param reqTaskDoComment the req task do comment
	 * @return the task
	 */
	public Task doCommentTask(String taskId, @Valid ReqTaskDoComment reqTaskDoComment);

	/**
	 * Do reply comment task.
	 *
	 * @param taskId the task id
	 * @param parentCommentId the parent comment id
	 * @param reqTaskDoComment the req task do comment
	 * @return the task
	 */
	public Task doReplyCommentTask(String taskId, String parentCommentId, @Valid ReqTaskDoComment reqTaskDoComment);
	
	/**
	 * Do assign user assignee.
	 *
	 * @param taskId the task id
	 * @param organizationId the organization id
	 * @param reqTaskDoAssignUserAssignee the req task do assign user assignee
	 * @return the task
	 */
	public Task doAssignUserAssignee(String taskId, String organizationId, @Valid ReqTaskDoAssignUserAssignee reqTaskDoAssignUserAssignee);
	
	/**
	 * Do un assign user assignee.
	 *
	 * @param taskId the task id
	 * @param organizationId the organization id
	 * @param reqTaskDoUnAssignUserAssignee the req task do un assign user assignee
	 * @return the task
	 */
	public Task doUnAssignUserAssignee(String taskId, String organizationId, @Valid ReqTaskDoUnAssignUserAssignee reqTaskDoUnAssignUserAssignee);
	
	/**
	 * Do assign user support.
	 *
	 * @param taskId the task id
	 * @param organizationId the organization id
	 * @param reqTaskDoAssignUserSupport the req task do assign user support
	 * @return the task
	 */
	public Task doAssignUserSupport(String taskId, String organizationId, @Valid ReqTaskDoAssignUserSupport reqTaskDoAssignUserSupport);
	
	/**
	 * Do un assign user support.
	 *
	 * @param taskId the task id
	 * @param organizationId the organization id
	 * @param reqTaskDoUnAssignUserSupport the req task do un assign user support
	 * @return the task
	 */
	public Task doUnAssignUserSupport(String taskId, String organizationId, @Valid ReqTaskDoUnAssignUserSupport reqTaskDoUnAssignUserSupport);
	
	
	/**
	 * Plus count sub task.
	 *
	 * @param taskId the task id
	 * @return the int
	 */
	public long plusCountSubTask(String taskId);
	
	/**
	 * Minus count sub task.
	 *
	 * @param taskId the task id
	 * @return the int
	 */
	public long minusCountSubTask(String taskId);
	
	/**
	 * Gets the count task of doc.
	 *
	 * @param docId the doc id
	 * @return the count task of doc
	 */
	public long getCountTaskOfDoc(String docId);
	
	/**
	 * Gets the count sub task.
	 *
	 * @param taskId the task id
	 * @return the count sub task
	 */
	public long getCountSubTask(String taskId);
	
	/**
	 * Kiểm tra Người dùng trong Đơn vị có liên quan đến nhiệm vụ không 
	 * @param organizationId
	 * @param userId
	 * @return
	 */
	public boolean isReferenceAnyTask(String organizationId, String userId);
	

}

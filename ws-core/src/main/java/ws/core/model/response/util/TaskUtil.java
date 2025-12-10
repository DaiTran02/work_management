package ws.core.model.response.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ws.core.enums.NotificationAction;
import ws.core.enums.NotificationObject;
import ws.core.enums.NotificationScope;
import ws.core.enums.NotificationType;
import ws.core.enums.TaskStatus;
import ws.core.model.Task;
import ws.core.model.embeded.Creator;
import ws.core.model.embeded.Receiver;
import ws.core.model.embeded.TaskComment;
import ws.core.model.embeded.TaskFollower;
import ws.core.model.embeded.TaskSupport;
import ws.core.model.filter.TaskFilter;
import ws.core.model.request.ReqNotificationCreate;
import ws.core.model.response.data.TaskCountByStatusModel;
import ws.core.services.NotificationService;
import ws.core.services.TaskService;

@Component
public class TaskUtil {

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskCommentUtil taskCommentUtil;

	@Autowired
	private NotificationService notificationService;

	private Document toCommon(Task task) {
		Document document=new Document();
		document.put("id", task.getId());
		document.put("createdTime", task.getCreatedTimeLong());
		document.put("updatedTime", task.getUpdatedTimeLong());
		document.put("docId", task.getDocId());
		document.put("parentId", task.getParentId());
		document.put("owner", task.getOwner());
		document.put("assistant", task.getAssistant());
		document.put("assignee", task.getAssignee());
		document.put("supports", task.getSupports());
		document.put("followers", task.getFollowers());
		document.put("priority", task.getPriority());
		document.put("title", task.getTitle());
		document.put("description", task.getDescription());
		document.put("endTime", task.getEndTimeLong());
		document.put("startingTime", task.getStartingTimeLong());
		document.put("requiredConfirm", task.isRequiredConfirm());
		
		/* Báo cáo hoàn thành */
		document.put("reported", task.getReported());
		document.put("reportedHistories", task.getReportedHistories());
		
		/* Từ chối báo cáo hoàn thành */
		document.put("confirmRefuse", task.getConfirmRefuse());
		document.put("confirmRefuseHistories", task.getConfirmRefuseHistories());
		
		document.put("completed", task.getCompleted());
		document.put("state", task.getState());
		document.put("status", task.getStatus());
		document.put("countSubTask", task.getCountSubTask());
		document.put("attachments", task.getAttachments());
		document.put("processes", task.getProcesses());
		document.put("comments", getComments(task.getComments()));
		document.put("reminds", task.getReminds());
		document.put("events", task.getEvents());
		document.put("rating", task.getRating());

		document.put("redo", task.getRedo());
		document.put("redoHistories", task.getRedoHistories());
		
		document.put("pending", task.getPending());
		document.put("pendingHistories", task.getPendingHistories());

		/* Từ chối thực hiện nhiệm vụ */
		document.put("refuse", task.getRefuse());
		document.put("refuseHistories", task.getRefuseHitories());
		
		/* Triệu hồi nhiệm vụ */
		document.put("reverse", task.getReverse());
		document.put("reverseHistories", task.getReverseHistories());

		document.put("notify", task.getNotify());
		document.put("docInfo", task.getDocInfo());
		document.put("docReferences", task.getDocReferences());
		document.put("syncSourceExternal", task.getSyncSourceExternal());
		document.put("requiredKpi", task.isRequiredKpi());
		document.put("kpi", task.isRequiredKpi());
		return document;
	}

	public Document toListSiteResponse(Task task) {
		Document document=toCommon(task);
		return document;
	}

	public Document toDetailSiteResponse(Task task) {
		Document document=toCommon(task);
		return document;
	}

	public List<Document> getComments(LinkedList<TaskComment> comments){
		return comments.stream().map(e->taskCommentUtil.toSiteReponse(e)).collect(Collectors.toList());
	}

	public List<Document> getReliesComment(LinkedList<TaskComment> comments, String parentCommentId){
		Optional<TaskComment> findTaskComment = comments.stream().filter(e->e.getId().equals(parentCommentId)).findFirst();
		if(findTaskComment.isPresent()) {
			return findTaskComment.get().getReplies().stream().map(e->taskCommentUtil.toSiteReponse(e)).collect(Collectors.toList());
		}
		return Arrays.asList();
	}

	public List<TaskCountByStatusModel> buildDataSummaryTasks(TaskFilter taskFilter){
		/* Data đang thực hiện */
		List<TaskCountByStatusModel> dangthuchien_status=new ArrayList<TaskCountByStatusModel>();
		dangthuchien_status.add(countByStatus(TaskStatus.dangthuchien_tronghan, taskFilter));
		dangthuchien_status.add(countByStatus(TaskStatus.dangthuchien_sapquahan, taskFilter));
		dangthuchien_status.add(countByStatus(TaskStatus.dangthuchien_quahan, taskFilter));
		dangthuchien_status.add(countByStatus(TaskStatus.dangthuchien_khonghan, taskFilter));
		TaskCountByStatusModel dangthuchien=countByStatus(TaskStatus.dangthuchien, taskFilter);
		dangthuchien.setChild(dangthuchien_status);

		/* Data chờ xác nhận */
		List<TaskCountByStatusModel> choxacnhan_status=new ArrayList<TaskCountByStatusModel>();
		choxacnhan_status.add(countByStatus(TaskStatus.choxacnhan_tronghan, taskFilter));
		choxacnhan_status.add(countByStatus(TaskStatus.choxacnhan_quahan, taskFilter));
		choxacnhan_status.add(countByStatus(TaskStatus.choxacnhan_khonghan, taskFilter));
		choxacnhan_status.add(countByStatus(TaskStatus.tuchoixacnhan, taskFilter));
		TaskCountByStatusModel choxacnhan=countByStatus(TaskStatus.choxacnhan, taskFilter);
		choxacnhan.setChild(choxacnhan_status);

		/* Data đã hoàn thành */
		List<TaskCountByStatusModel> dahoanthanh_status=new ArrayList<TaskCountByStatusModel>();
		dahoanthanh_status.add(countByStatus(TaskStatus.dahoanthanh_tronghan, taskFilter));
		dahoanthanh_status.add(countByStatus(TaskStatus.dahoanthanh_quahan, taskFilter));
		dahoanthanh_status.add(countByStatus(TaskStatus.dahoanthanh_khonghan, taskFilter));
		TaskCountByStatusModel dahoanthanh=countByStatus(TaskStatus.dahoanthanh, taskFilter);
		dahoanthanh.setChild(dahoanthanh_status);

		/* Data khác */
		List<TaskCountByStatusModel> khac_status=new ArrayList<TaskCountByStatusModel>();
		khac_status.add(countByStatus(TaskStatus.chuathuchien, taskFilter));
		khac_status.add(countByStatus(TaskStatus.tuchoithuchien, taskFilter));
		khac_status.add(countByStatus(TaskStatus.tamhoan, taskFilter));
		khac_status.add(countByStatus(TaskStatus.thuchienlai, taskFilter));
		TaskCountByStatusModel khacModel = new TaskCountByStatusModel();
		khacModel.setKey("khac");
		khacModel.setName("Khác");
		khacModel.setShortName("Khác");
		khacModel.setCount(getSumCount(khac_status));
		khacModel.setChild(khac_status);

		/* Result */
		List<TaskCountByStatusModel> result=new ArrayList<TaskCountByStatusModel>();
		result.add(dangthuchien);
		result.add(choxacnhan);
		result.add(dahoanthanh);
		result.add(khacModel);

		return result;
	}

	public TaskCountByStatusModel countByStatus(TaskStatus taskStatus, TaskFilter taskFilter) {
		taskFilter.setStatus(taskStatus);

		TaskCountByStatusModel document=new TaskCountByStatusModel();
		document.setKey(taskStatus.getKey());
		document.setName(taskStatus.getName());
		document.setShortName(taskStatus.getShortName());
		document.setCount(taskService.countTaskAll(taskFilter));
		return document;
	}

	public long getSumCount(List<TaskCountByStatusModel> documents) {
		long count=0;
		for (TaskCountByStatusModel document : documents) {
			count+=document.getCount();
		}
		return count;
	}

	public List<Document> buildDataAchivementTasks(TaskFilter taskFilter){
		taskFilter.setStatus(TaskStatus.dahoanthanh_tronghan);
		Document document1=new Document();
		document1.put("name", "Số nhiệm vụ hoàn thành trước hạn");
		document1.put("value", taskService.countTaskAll(taskFilter));

		taskFilter.setStatus(TaskStatus.dahoanthanh);
		taskFilter.setRatingStar(Arrays.asList(3,4,5));
		Document document2=new Document();
		document2.put("name", "Số nhiệm vụ được đánh giá cao (trên 3 sao)");
		document2.put("value", taskService.countTaskAll(taskFilter));

		List<Document> result=new ArrayList<Document>();
		result.add(document1);
		result.add(document2);
		return result;
	}

	public void notificationCreateTask(Task task) {
		try {
			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_moi_duoc_giao_xu_ly);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_moi_duoc_giao_xu_ly.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(task.getId());
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());

			reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Assignee.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Assignee);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_moi_duoc_giao_phoi_hop);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_moi_duoc_giao_phoi_hop.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					reqNotificationCreate_Support.setScope(NotificationScope.user);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_moi_duoc_giao_theo_doi);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_moi_duoc_giao_theo_doi.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Support.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notificationUpdateTask(Task task) {
		try {
			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_duoc_cap_nhat);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_duoc_cap_nhat.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(task.getId());
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());

			reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Assignee.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Assignee);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_duoc_cap_nhat);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_duoc_cap_nhat.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_duoc_cap_nhat);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_duoc_cap_nhat.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notificationDeleteTask(Task task) {
		try {
			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_da_bi_xoa);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_da_bi_xoa.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(null);
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());

			reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Assignee.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Assignee);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_da_bi_xoa);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_da_bi_xoa.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(null);
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_da_bi_xoa);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_da_bi_xoa.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(null);
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ xử lý từ chối thực hiện nhiệm vụ
	 * @param task
	 */
	public void notificationTask_DoRefuse(Task task) {
		try {
			/* Thông báo cho người giao */
			ReqNotificationCreate reqNotificationCreate_Owner=new ReqNotificationCreate();
			reqNotificationCreate_Owner.setAction(NotificationAction.nhiem_vu_bi_tu_choi_thuc_hien);
			reqNotificationCreate_Owner.setTitle(NotificationAction.nhiem_vu_bi_tu_choi_thuc_hien.getTitle());
			reqNotificationCreate_Owner.setContent(task.getTitle());
			reqNotificationCreate_Owner.setType(NotificationType.info);
			reqNotificationCreate_Owner.setObject(NotificationObject.task);
			reqNotificationCreate_Owner.setObjectId(task.getId());
			reqNotificationCreate_Owner.setActionUrl(null);
			reqNotificationCreate_Owner.setCreator(null);

			Receiver receiver_Owner=new Receiver();
			receiver_Owner.setOrganizationId(task.getOwner().getOrganizationId());
			receiver_Owner.setOrganizationName(task.getOwner().getOrganizationName());
			receiver_Owner.setOrganizationUserId(task.getOwner().getOrganizationUserId());
			receiver_Owner.setOrganizationUserName(task.getOwner().getOrganizationUserName());

			reqNotificationCreate_Owner.setReceiver(receiver_Owner);
			if(receiver_Owner.getOrganizationUserId()!=null) {
				reqNotificationCreate_Owner.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Owner.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Owner);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_bi_tu_choi_thuc_hien);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_bi_tu_choi_thuc_hien.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_bi_tu_choi_thuc_hien);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_bi_tu_choi_thuc_hien.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ xử lý báo cáo tiến độ
	 * @param task
	 */
	public void notificationTask_DoUpdateProcess(Task task) {
		try {
			/* Thông báo cho người giao */
			ReqNotificationCreate reqNotificationCreate_Owner=new ReqNotificationCreate();
			reqNotificationCreate_Owner.setAction(NotificationAction.nhiem_vu_duoc_cap_nhat_tien_do);
			reqNotificationCreate_Owner.setTitle(NotificationAction.nhiem_vu_duoc_cap_nhat_tien_do.getTitle());
			reqNotificationCreate_Owner.setContent(task.getTitle());
			reqNotificationCreate_Owner.setType(NotificationType.info);
			reqNotificationCreate_Owner.setObject(NotificationObject.task);
			reqNotificationCreate_Owner.setObjectId(task.getId());
			reqNotificationCreate_Owner.setActionUrl(null);
			reqNotificationCreate_Owner.setCreator(null);

			Receiver receiver_Owner=new Receiver();
			receiver_Owner.setOrganizationId(task.getOwner().getOrganizationId());
			receiver_Owner.setOrganizationName(task.getOwner().getOrganizationName());
			receiver_Owner.setOrganizationUserId(task.getOwner().getOrganizationUserId());
			receiver_Owner.setOrganizationUserName(task.getOwner().getOrganizationUserName());

			reqNotificationCreate_Owner.setReceiver(receiver_Owner);
			if(receiver_Owner.getOrganizationUserId()!=null) {
				reqNotificationCreate_Owner.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Owner.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Owner);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_duoc_cap_nhat_tien_do);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_duoc_cap_nhat_tien_do.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_duoc_cap_nhat_tien_do);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_duoc_cap_nhat_tien_do.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ xử lý nhiệm vụ bắt đầu thực hiện
	 * @param task
	 */
	public void notificationTask_DoAccept(Task task) {
		try {
			/* Thông báo cho người giao */
			ReqNotificationCreate reqNotificationCreate_Owner=new ReqNotificationCreate();
			reqNotificationCreate_Owner.setAction(NotificationAction.nhiem_vu_bat_dau_thuc_hien);
			reqNotificationCreate_Owner.setTitle(NotificationAction.nhiem_vu_bat_dau_thuc_hien.getTitle());
			reqNotificationCreate_Owner.setContent(task.getTitle());
			reqNotificationCreate_Owner.setType(NotificationType.info);
			reqNotificationCreate_Owner.setObject(NotificationObject.task);
			reqNotificationCreate_Owner.setObjectId(task.getId());
			reqNotificationCreate_Owner.setActionUrl(null);
			reqNotificationCreate_Owner.setCreator(null);

			Receiver receiver_Owner=new Receiver();
			receiver_Owner.setOrganizationId(task.getOwner().getOrganizationId());
			receiver_Owner.setOrganizationName(task.getOwner().getOrganizationName());
			receiver_Owner.setOrganizationUserId(task.getOwner().getOrganizationUserId());
			receiver_Owner.setOrganizationUserName(task.getOwner().getOrganizationUserName());

			reqNotificationCreate_Owner.setReceiver(receiver_Owner);
			if(receiver_Owner.getOrganizationUserId()!=null) {
				reqNotificationCreate_Owner.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Owner.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Owner);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_bat_dau_thuc_hien);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_bat_dau_thuc_hien.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_bat_dau_thuc_hien);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_bat_dau_thuc_hien.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ xử lý yêu cầu Đơn vị/Cán bộ giao xác nhận hoàn thành nhiệm vụ
	 * @param task
	 */
	public void notificationTask_DoReport(Task task) {
		try {
			/* Thông báo cho người giao */
			ReqNotificationCreate reqNotificationCreate_Owner=new ReqNotificationCreate();
			reqNotificationCreate_Owner.setAction(NotificationAction.nhiem_vu_yeu_cau_xac_nhan_hoan_thanh);
			reqNotificationCreate_Owner.setTitle(NotificationAction.nhiem_vu_yeu_cau_xac_nhan_hoan_thanh.getTitle());
			reqNotificationCreate_Owner.setContent(task.getTitle());
			reqNotificationCreate_Owner.setType(NotificationType.info);
			reqNotificationCreate_Owner.setObject(NotificationObject.task);
			reqNotificationCreate_Owner.setObjectId(task.getId());
			reqNotificationCreate_Owner.setActionUrl(null);
			reqNotificationCreate_Owner.setCreator(null);

			Receiver receiver_Owner=new Receiver();
			receiver_Owner.setOrganizationId(task.getOwner().getOrganizationId());
			receiver_Owner.setOrganizationName(task.getOwner().getOrganizationName());
			receiver_Owner.setOrganizationUserId(task.getOwner().getOrganizationUserId());
			receiver_Owner.setOrganizationUserName(task.getOwner().getOrganizationUserName());

			reqNotificationCreate_Owner.setReceiver(receiver_Owner);
			if(receiver_Owner.getOrganizationUserId()!=null) {
				reqNotificationCreate_Owner.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Owner.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Owner);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_cho_xac_nhan_hoan_thanh);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_cho_xac_nhan_hoan_thanh.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_yeu_cau_xac_nhan_hoan_thanh);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_yeu_cau_xac_nhan_hoan_thanh.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ xử lý triệu hồi lại nhiệm vụ đã báo cáo/hoàn thành
	 * @param task
	 */
	public void notificationTask_DoReverse(Task task) {
		try {
			/* Thông báo cho người giao */
			ReqNotificationCreate reqNotificationCreate_Owner=new ReqNotificationCreate();
			reqNotificationCreate_Owner.setAction(NotificationAction.nhiem_vu_duoc_trieu_hoi);
			reqNotificationCreate_Owner.setTitle(NotificationAction.nhiem_vu_duoc_trieu_hoi.getTitle());
			reqNotificationCreate_Owner.setContent(task.getTitle());
			reqNotificationCreate_Owner.setType(NotificationType.info);
			reqNotificationCreate_Owner.setObject(NotificationObject.task);
			reqNotificationCreate_Owner.setObjectId(task.getId());
			reqNotificationCreate_Owner.setActionUrl(null);
			reqNotificationCreate_Owner.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getOwner().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getOwner().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getOwner().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getOwner().getOrganizationUserName());

			reqNotificationCreate_Owner.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Owner.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Owner.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Owner);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_duoc_trieu_hoi);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_duoc_trieu_hoi.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_duoc_trieu_hoi);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_duoc_trieu_hoi.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ xử lý hoàn thành nhiệm vụ (không cần xác nhận)
	 * @param task
	 */
	public void notificationTask_DoComplete(Task task) {
		try {
			/* Thông báo cho người giao */
			ReqNotificationCreate reqNotificationCreate_Owner=new ReqNotificationCreate();
			reqNotificationCreate_Owner.setAction(NotificationAction.nhiem_vu_duoc_hoan_thanh);
			reqNotificationCreate_Owner.setTitle(NotificationAction.nhiem_vu_duoc_hoan_thanh.getTitle());
			reqNotificationCreate_Owner.setContent(task.getTitle());
			reqNotificationCreate_Owner.setType(NotificationType.info);
			reqNotificationCreate_Owner.setObject(NotificationObject.task);
			reqNotificationCreate_Owner.setObjectId(task.getId());
			reqNotificationCreate_Owner.setActionUrl(null);
			reqNotificationCreate_Owner.setCreator(null);

			Receiver receiver_Owner=new Receiver();
			receiver_Owner.setOrganizationId(task.getOwner().getOrganizationId());
			receiver_Owner.setOrganizationName(task.getOwner().getOrganizationName());
			receiver_Owner.setOrganizationUserId(task.getOwner().getOrganizationUserId());
			receiver_Owner.setOrganizationUserName(task.getOwner().getOrganizationUserName());

			reqNotificationCreate_Owner.setReceiver(receiver_Owner);
			if(receiver_Owner.getOrganizationUserId()!=null) {
				reqNotificationCreate_Owner.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Owner.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Owner);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_duoc_hoan_thanh);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_duoc_hoan_thanh.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_duoc_hoan_thanh);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_duoc_hoan_thanh.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Đơn vị/Cán bộ giao nhiệm vụ tạm hoãn nhiệm vụ
	 * @param task
	 */
	public void notificationTask_DoPending(Task task) {
		try {
			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_bi_tam_hoan);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_bi_tam_hoan.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(task.getId());
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());

			reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Assignee.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Assignee);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_bi_tam_hoan);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_bi_tam_hoan.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_bi_tam_hoan);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_bi_tam_hoan.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ giao nhiệm vụ hủy tạm hoãn
	 * @param task
	 */
	public void notificationTask_DoUnPending(Task task) {
		try {
			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_tiep_tuc_thuc_hien);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_tiep_tuc_thuc_hien.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(task.getId());
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());

			reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Assignee.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Assignee);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_tiep_tuc_thuc_hien);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_tiep_tuc_thuc_hien.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_tiep_tuc_thuc_hien);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_tiep_tuc_thuc_hien.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ giao nhiệm vụ đánh giá nhiệm vụ
	 * @param task
	 */
	public void notificationTask_DoRating(Task task) {
		try {
			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_duoc_danh_gia);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_duoc_danh_gia.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(task.getId());
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());

			reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Assignee.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Assignee);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_duoc_danh_gia);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_duoc_danh_gia.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_duoc_danh_gia);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_duoc_danh_gia.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ giao nhiệm vụ yêu cầu thực hiện lại nhiệm vụ
	 * @param task
	 */
	public void notificationTask_DoUndo(Task task) {
		try {
			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_duoc_yeu_cau_thuc_hien_lai);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_duoc_yeu_cau_thuc_hien_lai.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(task.getId());
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());

			reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Assignee.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Assignee);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_duoc_yeu_cau_thuc_hien_lai);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_duoc_yeu_cau_thuc_hien_lai.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_duoc_yeu_cau_thuc_hien_lai);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_duoc_yeu_cau_thuc_hien_lai.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ giao nhiệm vụ nhắc nhở các đơn vị thực hiện
	 * @param task
	 */
	public void notificationTask_DoRemind(Task task) {
		try {
			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_duoc_nhac_nho);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_duoc_nhac_nho.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(task.getId());
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());

			reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Assignee.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Assignee);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_duoc_nhac_nho);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_duoc_nhac_nho.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_duoc_nhac_nho);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_duoc_nhac_nho.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Các Đơn vị/Cán bộ trao đổi ý kiến
	 * @param task
	 */
	public void notificationTask_DoComment(Task task, Creator creator) {
		try {
			/* Thông báo cho người giao nhiệm vụ */
			ReqNotificationCreate reqNotificationCreate_Owner=new ReqNotificationCreate();
			reqNotificationCreate_Owner.setAction(NotificationAction.nhiem_vu_co_phan_hoi_trao_doi_moi);
			reqNotificationCreate_Owner.setTitle(NotificationAction.nhiem_vu_co_phan_hoi_trao_doi_moi.getTitle());
			reqNotificationCreate_Owner.setContent(task.getTitle());
			reqNotificationCreate_Owner.setType(NotificationType.info);
			reqNotificationCreate_Owner.setObject(NotificationObject.task);
			reqNotificationCreate_Owner.setObjectId(task.getId());
			reqNotificationCreate_Owner.setActionUrl(null);
			reqNotificationCreate_Owner.setCreator(null);

			Receiver receiver_Owner=new Receiver();
			receiver_Owner.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Owner.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Owner.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Owner.setOrganizationUserName(task.getAssignee().getOrganizationUserName());
			if(creator.hashCode()!=receiver_Owner.hashCode()) {
				reqNotificationCreate_Owner.setReceiver(receiver_Owner);
				if(receiver_Owner.getOrganizationUserId()!=null) {
					reqNotificationCreate_Owner.setScope(NotificationScope.user);
				}else {
					reqNotificationCreate_Owner.setScope(NotificationScope.organization);
				}
				notificationService.create(reqNotificationCreate_Owner);
			}

			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_co_phan_hoi_trao_doi_moi);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_co_phan_hoi_trao_doi_moi.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(task.getId());
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());
			if(creator.hashCode()!=receiver_Assignee.hashCode()) {
				reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
				if(receiver_Assignee.getOrganizationUserId()!=null) {
					reqNotificationCreate_Assignee.setScope(NotificationScope.user);
				}else {
					reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
				}
				notificationService.create(reqNotificationCreate_Assignee);
			}

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_co_phan_hoi_trao_doi_moi);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_co_phan_hoi_trao_doi_moi.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					if(creator.hashCode()!=receiver_Support.hashCode()) {
						reqNotificationCreate_Support.setReceiver(receiver_Support);
						if(receiver_Support.getOrganizationUserId()!=null) {
							reqNotificationCreate_Support.setScope(NotificationScope.user);
						}else {
							reqNotificationCreate_Support.setScope(NotificationScope.organization);
						}
						notificationService.create(reqNotificationCreate_Support);
					}
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_co_phan_hoi_trao_doi_moi);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_co_phan_hoi_trao_doi_moi.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					if(creator.hashCode()!=receiver_Follower.hashCode()) {
						reqNotificationCreate_Follower.setReceiver(receiver_Follower);
						if(receiver_Follower.getOrganizationUserId()!=null) {
							reqNotificationCreate_Follower.setScope(NotificationScope.user);
						}else {
							reqNotificationCreate_Follower.setScope(NotificationScope.organization);
						}
						notificationService.create(reqNotificationCreate_Follower);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Đơn vị/Cán bộ giao nhiệm vụ xác nhận nhiệm vụ đã hoàn thành
	 * @param task
	 */
	public void notificationTask_DoConfirm(Task task) {
		try {
			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_da_xac_nhan_hoan_thanh);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_da_xac_nhan_hoan_thanh.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(task.getId());
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());

			reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Assignee.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Assignee);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_da_xac_nhan_hoan_thanh);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_da_xac_nhan_hoan_thanh.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_da_xac_nhan_hoan_thanh);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_da_xac_nhan_hoan_thanh.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notificationTask_DoConfirmRefuse(Task task) {
		try {
			/* Thông báo cho người xử lý */
			ReqNotificationCreate reqNotificationCreate_Assignee=new ReqNotificationCreate();
			reqNotificationCreate_Assignee.setAction(NotificationAction.nhiem_vu_bi_tu_choi_xac_nhan_hoan_thanh);
			reqNotificationCreate_Assignee.setTitle(NotificationAction.nhiem_vu_bi_tu_choi_xac_nhan_hoan_thanh.getTitle());
			reqNotificationCreate_Assignee.setContent(task.getTitle());
			reqNotificationCreate_Assignee.setType(NotificationType.info);
			reqNotificationCreate_Assignee.setObject(NotificationObject.task);
			reqNotificationCreate_Assignee.setObjectId(task.getId());
			reqNotificationCreate_Assignee.setActionUrl(null);
			reqNotificationCreate_Assignee.setCreator(null);

			Receiver receiver_Assignee=new Receiver();
			receiver_Assignee.setOrganizationId(task.getAssignee().getOrganizationId());
			receiver_Assignee.setOrganizationName(task.getAssignee().getOrganizationName());
			receiver_Assignee.setOrganizationUserId(task.getAssignee().getOrganizationUserId());
			receiver_Assignee.setOrganizationUserName(task.getAssignee().getOrganizationUserName());

			reqNotificationCreate_Assignee.setReceiver(receiver_Assignee);
			if(receiver_Assignee.getOrganizationUserId()!=null) {
				reqNotificationCreate_Assignee.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Assignee.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Assignee);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_bi_tu_choi_xac_nhan_hoan_thanh);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_bi_tu_choi_xac_nhan_hoan_thanh.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_bi_tu_choi_xac_nhan_hoan_thanh);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_bi_tu_choi_xac_nhan_hoan_thanh.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notificationTask_DoRedoAndReportAgain(Task task) {
		try {
			/* Thông báo cho người giao */
			ReqNotificationCreate reqNotificationCreate_Owner=new ReqNotificationCreate();
			reqNotificationCreate_Owner.setAction(NotificationAction.nhiem_vu_thuc_hien_va_bao_cao_lai);
			reqNotificationCreate_Owner.setTitle(NotificationAction.nhiem_vu_thuc_hien_va_bao_cao_lai.getTitle());
			reqNotificationCreate_Owner.setContent(task.getTitle());
			reqNotificationCreate_Owner.setType(NotificationType.info);
			reqNotificationCreate_Owner.setObject(NotificationObject.task);
			reqNotificationCreate_Owner.setObjectId(task.getId());
			reqNotificationCreate_Owner.setActionUrl(null);
			reqNotificationCreate_Owner.setCreator(null);

			Receiver receiver_Owner=new Receiver();
			receiver_Owner.setOrganizationId(task.getOwner().getOrganizationId());
			receiver_Owner.setOrganizationName(task.getOwner().getOrganizationName());
			receiver_Owner.setOrganizationUserId(task.getOwner().getOrganizationUserId());
			receiver_Owner.setOrganizationUserName(task.getOwner().getOrganizationUserName());

			reqNotificationCreate_Owner.setReceiver(receiver_Owner);
			if(receiver_Owner.getOrganizationUserId()!=null) {
				reqNotificationCreate_Owner.setScope(NotificationScope.user);
			}else {
				reqNotificationCreate_Owner.setScope(NotificationScope.organization);
			}
			notificationService.create(reqNotificationCreate_Owner);

			/* Thông báo cho người phối hợp nếu có */
			if(task.getSupports().size()>0) {
				for(TaskSupport taskSupport:task.getSupports()) {
					ReqNotificationCreate reqNotificationCreate_Support=new ReqNotificationCreate();
					reqNotificationCreate_Support.setAction(NotificationAction.nhiem_vu_thuc_hien_va_bao_cao_lai);
					reqNotificationCreate_Support.setTitle(NotificationAction.nhiem_vu_thuc_hien_va_bao_cao_lai.getTitle());
					reqNotificationCreate_Support.setContent(task.getTitle());
					reqNotificationCreate_Support.setType(NotificationType.info);
					reqNotificationCreate_Support.setObject(NotificationObject.task);
					reqNotificationCreate_Support.setObjectId(task.getId());
					reqNotificationCreate_Support.setActionUrl(null);
					reqNotificationCreate_Support.setCreator(null);

					Receiver receiver_Support=new Receiver();
					receiver_Support.setOrganizationId(taskSupport.getOrganizationId());
					receiver_Support.setOrganizationName(taskSupport.getOrganizationName());
					receiver_Support.setOrganizationUserId(taskSupport.getOrganizationUserId());
					receiver_Support.setOrganizationUserName(taskSupport.getOrganizationUserName());

					reqNotificationCreate_Support.setReceiver(receiver_Support);
					if(receiver_Support.getOrganizationUserId()!=null) {
						reqNotificationCreate_Support.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Support.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Support);
				}
			}

			/* Thông báo cho người theo dõi nếu có */
			if(task.getSupports().size()>0) {
				for(TaskFollower taskFollower:task.getFollowers()) {
					ReqNotificationCreate reqNotificationCreate_Follower=new ReqNotificationCreate();
					reqNotificationCreate_Follower.setAction(NotificationAction.nhiem_vu_thuc_hien_va_bao_cao_lai);
					reqNotificationCreate_Follower.setTitle(NotificationAction.nhiem_vu_thuc_hien_va_bao_cao_lai.getTitle());
					reqNotificationCreate_Follower.setContent(task.getTitle());
					reqNotificationCreate_Follower.setType(NotificationType.info);
					reqNotificationCreate_Follower.setObject(NotificationObject.task);
					reqNotificationCreate_Follower.setObjectId(task.getId());
					reqNotificationCreate_Follower.setActionUrl(null);
					reqNotificationCreate_Follower.setCreator(null);

					Receiver receiver_Follower=new Receiver();
					receiver_Follower.setOrganizationId(taskFollower.getOrganizationId());
					receiver_Follower.setOrganizationName(taskFollower.getOrganizationName());
					receiver_Follower.setOrganizationUserId(taskFollower.getOrganizationUserId());
					receiver_Follower.setOrganizationUserName(taskFollower.getOrganizationUserName());

					reqNotificationCreate_Follower.setReceiver(receiver_Follower);
					if(receiver_Follower.getOrganizationUserId()!=null) {
						reqNotificationCreate_Follower.setScope(NotificationScope.user);
					}else {
						reqNotificationCreate_Follower.setScope(NotificationScope.organization);
					}
					notificationService.create(reqNotificationCreate_Follower);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package ws.core.model.response.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ws.core.enums.NotificationAction;
import ws.core.enums.NotificationObject;
import ws.core.enums.NotificationScope;
import ws.core.enums.NotificationType;
import ws.core.enums.TaskStatus;
import ws.core.model.Doc;
import ws.core.model.Task;
import ws.core.model.embeded.Receiver;
import ws.core.model.filter.TaskFilter;
import ws.core.model.request.ReqNotificationCreate;
import ws.core.services.NotificationService;
import ws.core.services.TaskService;

@Component
public class DocUtil {
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private NotificationService notificationService;
	
	private Document toCommon(Doc doc) {
		Document document=new Document();
		document.put("id", doc.getId());
		document.put("createdTime", doc.getCreatedTimeLong());
		document.put("updatedTime", doc.getUpdatedTimeLong());
		document.put("number", doc.getNumber());
		document.put("symbol", doc.getSymbol());
		document.put("security", doc.getSecurity());
		document.put("regDate", doc.getRegDateLong());
		document.put("type", doc.getType());
		document.put("signerName", doc.getSignerName());
		document.put("signerPosition", doc.getSignerPosition());
		document.put("copies", doc.getCopies());
		document.put("pages", doc.getPages());
		document.put("orgReceiveName", doc.getOrgReceiveName());
		document.put("orgCreateName", doc.getOrgCreateName());
		document.put("summary", doc.getSummary());
		document.put("owner", doc.getOwner());
		document.put("receivers", doc.getReceivers());
		document.put("category", doc.getCategory());
		document.put("external", doc.getExternal());
		document.put("attachments", doc.getAttachments());
		document.put("creatorId", doc.getCreatorId());
		document.put("creatorName", doc.getCreatorName());
		document.put("active", doc.isActive());
		document.put("trash", doc.isTrash());
		document.put("status", doc.getStatus());
		document.put("countTask", doc.getCountTask());
		
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setDocIds(Arrays.asList(doc.getId()));
		taskFilter.setTaskRoot(true);
		taskFilter.setStatus(TaskStatus.dahoanthanh);
		document.put("countTaskCompleted", taskService.countTaskAll(taskFilter));
		document.put("resultConfirm", doc.getResultConfirm());
		
		document.put("classifyTaskId", doc.getClassifyTaskId());
		document.put("leaderApproveTaskId", doc.getLeaderApproveTaskId());
		document.put("iOfficeId", doc.getIOfficeId());
		document.put("source", doc.getSource());
		
		return document;
	}
	
	public Document toSiteResponse(Doc doc) {
		Document document=toCommon(doc);
		return document;
	}
	
	public List<Document> toSiteResponseTreeTasks(Doc doc){
		List<Document> treeTasks=new ArrayList<>();
		
		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setDocIds(Arrays.asList(doc.getId()));
		taskFilter.setTaskRoot(true);
		
		List<Task> tasks = taskService.findTaskAll(taskFilter);
		for (Task task : tasks) {
			try {
				treeTasks.add(buildMetaChildTasks(task));
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return treeTasks;
	} 
	
	private Document buildMetaChildTasks(Task task) throws InterruptedException, ExecutionException{
		Document taskCurrent=new Document();
		taskCurrent.put("id", task.getId());
		taskCurrent.put("docId", task.getDocId());
		taskCurrent.put("fromOrganization", task.getOwner());
		taskCurrent.put("toOrganization", task.getAssignee());
		taskCurrent.put("title", task.getTitle());
		taskCurrent.put("description", task.getDescription());
		taskCurrent.put("countSubTask", task.getCountSubTask());

		TaskFilter taskFilter=new TaskFilter();
		taskFilter.setParentIds(Arrays.asList(task.getId()));
		
		List<Task> childTasks = taskService.findTaskAll(taskFilter);
		if(childTasks.size()>0) {
			List<Document> childTasksDocument=new ArrayList<Document>();
			
			/* Khởi tạo threads */
			ExecutorService executor = Executors.newFixedThreadPool(childTasks.size());
			List<Future<Document>> listFuture = new ArrayList<Future<Document>>();
			
			for (Task chilTask : childTasks) {
				listFuture.add(executor.submit(new Callable<Document>() {
					@Override
					public Document call() throws Exception {
						return buildMetaChildTasks(chilTask);
					}
				}));
			}
			
			for (Future<Document> future : listFuture) {
				childTasksDocument.add(future.get());
			}
			
			executor.shutdown();
			taskCurrent.append("childTasks", childTasksDocument);
		}
		return taskCurrent;
	}
	
	/**
	 * Thông báo cho Người soạn văn bản đó biết khi được đồng bộ về
	 * @param doc
	 */
	public void notificationSyncDocToOwner(Doc doc) {
		 ReqNotificationCreate reqNotificationCreate=new ReqNotificationCreate();
		 reqNotificationCreate.setTitle("Đồng bộ văn bản từ VNPT");
		 reqNotificationCreate.setContent("Văn bản đã được đồng bộ về từ hệ thống QLVB");
		 reqNotificationCreate.setType(NotificationType.info);
		 reqNotificationCreate.setObject(NotificationObject.doc);
		 reqNotificationCreate.setObjectId(doc.getId());
		 reqNotificationCreate.setAction(NotificationAction.van_ban_moi_duoc_dong_bo);
		 reqNotificationCreate.setActionUrl(null);
		 reqNotificationCreate.setCreator(null);
		 
		 Receiver receiver=new Receiver();
		 receiver.setOrganizationId(doc.getOwner().getOrganizationId());
		 receiver.setOrganizationName(doc.getOwner().getOrganizationName());
		 receiver.setOrganizationUserId(doc.getOwner().getOrganizationUserId());
		 receiver.setOrganizationUserName(doc.getOwner().getOrganizationUserName());
		 
		 reqNotificationCreate.setReceiver(receiver);
		 if(receiver.getOrganizationUserId()!=null) {
			 reqNotificationCreate.setScope(NotificationScope.user);
		 }else {
			 reqNotificationCreate.setScope(NotificationScope.organization);
		 }
		 notificationService.create(reqNotificationCreate);
	}
}  

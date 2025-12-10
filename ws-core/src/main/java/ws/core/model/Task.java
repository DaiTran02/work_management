package ws.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.enums.TaskState;
import ws.core.model.embeded.TaskAssignee;
import ws.core.model.embeded.TaskAssistant;
import ws.core.model.embeded.TaskComment;
import ws.core.model.embeded.TaskCompleted;
import ws.core.model.embeded.TaskConfirmRefuse;
import ws.core.model.embeded.TaskDocInfo;
import ws.core.model.embeded.TaskDocReference;
import ws.core.model.embeded.TaskEvent;
import ws.core.model.embeded.TaskFollower;
import ws.core.model.embeded.TaskNotify;
import ws.core.model.embeded.TaskOwner;
import ws.core.model.embeded.TaskPending;
import ws.core.model.embeded.TaskProcess;
import ws.core.model.embeded.TaskRating;
import ws.core.model.embeded.TaskRedo;
import ws.core.model.embeded.TaskRefuse;
import ws.core.model.embeded.TaskRemind;
import ws.core.model.embeded.TaskReported;
import ws.core.model.embeded.TaskReverse;
import ws.core.model.embeded.TaskSupport;
import ws.core.model.embeded.TaskSyncSourceExternal;

@Data
@Document(collection = "task")
public class Task {
	/**
	 * id nhiệm vụ
	 */
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	/**
	 * Thời gian tạo nhiệm vụ
	 */
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	/**
	 * Thời gian cập nhật cuối
	 */
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	/**
	 * id văn bản giao nhiệm vụ
	 */
	@Indexed
	@Field(value = "docId")
	private String docId;
	
	/**
	 * id nhiệm vụ cấp trên
	 */
	@Indexed
	@Field(value = "parentId")
	private String parentId;
	
	/**
	 * Thông tin đơn vị giao
	 */
	@Indexed
	@Field(value = "owner")
	private TaskOwner owner;
	
	/**
	 * Thông tin tổ/người giao thay
	 */
	@Indexed
	@Field(value = "assistant")
	private TaskAssistant assistant;
	
	/**
	 * Thông tin đơn vị xử lý
	 */
	@Indexed
	@Field(value = "assignee")
	private TaskAssignee assignee;
	
	/**
	 * Danh sách thông tin đơn vị phối hợp
	 */
	@Indexed
	@Field(value = "supports")
	private List<TaskSupport> supports=new ArrayList<>();
	
	/**
	 * Danh sách thông tin khối theo dõi
	 */
	@Indexed
	@Field(value = "followers")
	private List<TaskFollower> followers=new ArrayList<>();
	
	/**
	 * Độ mật nhiệm vụ
	 */
	@Indexed
	@Field(value = "priority")
	private String priority=null; 
	
	/**
	 * Tiêu đề nhiệm vụ
	 */
	@Indexed
	@Field(value = "title")
	private String title=null;
	
	/**
	 * Nội dung giao nhiệm vụ
	 */
	@Indexed
	@Field(value = "description")
	public String description=null;
	
	/**
	 * Hạn xử lý nhiệm vụ
	 */
	@Indexed
	@Field(value = "endTime")
	private Date endTime=null;
	
	/**
	 * Thời gian ghi nhận bắt đầu thực hiện
	 */
	@Indexed
	@Field(value = "startingTime")
	private Date startingTime=null;
	
	/**
	 * Thời gian ghi nhận bắt đầu khi được yêu cầu thực hiện lại
	 */
	@Indexed
	@Field(value = "startingTimeAgain")
	private Date startingTimeAgain=null;
	
	/**
	 * Nhiệm vụ yêu cầu xác nhận hoàn thành
	 */
	@Indexed
	@Field(value = "requiredConfirm")
	private boolean requiredConfirm=false;
	
	/**
	 * Nhiệm vụ đánh giá KPI
	 */
	@Indexed
	@Field(value = "requiredKpi")
	private boolean requiredKpi=false;
	
	/**
	 * Thông tin báo cáo xác nhận hoàn thành
	 */
	@Indexed
	@Field(value = "reported")
	private TaskReported reported=null;
	
	@Field(value = "reportedHistories")
	private LinkedList<TaskReported> reportedHistories=new LinkedList<>();
	
	/**
	 * Thông tin xác nhận hoàn thành bị từ chối
	 */
	@Field(value = "confirmRefuse")
	private TaskConfirmRefuse confirmRefuse=null;
	
	/**
	 * Thông tin lịch sử xác nhận hoàn thành bị từ chối
	 */
	@Field(value = "confirmRefuseHistories")
	private LinkedList<TaskConfirmRefuse> confirmRefuseHistories=new LinkedList<>();
	
	/**
	 * Thông tin hoàn thành nhiệm vụ
	 */
	@Indexed
	@Field(value = "completed")
	private TaskCompleted completed=null;
	
	/**
	 * Thông tin triệu hồi nhiệm vụ
	 */
	@Field(value = "reverse")
	private TaskReverse reverse=null;
	
	/**
	 * Danh sách thông tin triệu hồi nhiệm vụ
	 */
	@Field(value = "reverseHistories")
	private LinkedList<TaskReverse> reverseHistories=new LinkedList<>();
	
	/**
	 * Trạng thái nhiệm vụ
	 */
	@Indexed
	@Field(value = "state")
	private String state=null;
	
	/**
	 * Trạng thái nhiệm vụ trước
	 */
	@Field(value = "statePrevious")
	private String statePrevious=null;
	
	/**
	 * Số nhiệm vụ con
	 */
	@Indexed
	@Field(value = "countSubTask")
	private int countSubTask=0;
	
	/**
	 * Danh sách đính kèm của nhiệm vụ
	 */
	@Indexed
	@Field(value = "attachments")
	private LinkedList<String> attachments=new LinkedList<>();
	
	/**
	 * Danh sách cập nhật tiến độ
	 */
	@Field(value = "processes")
	private LinkedList<TaskProcess> processes=new LinkedList<>();
	
	/**
	 * Danh sách trao đổi ý kiến
	 */
	@Field(value = "comments")
	private LinkedList<TaskComment> comments=new LinkedList<>();
	
	/**
	 * Danh sách nhắc nhở
	 */
	@Field(value = "reminds")
	private LinkedList<TaskRemind> reminds=new LinkedList<>();
	
	/**
	 * Danh sách nhật ký
	 */
	@Field(value = "events")
	private LinkedList<TaskEvent> events=new LinkedList<>();
	
	/**
	 * Thông tin đánh giá nhiệm vụ hoàn thành
	 */
	@Field(value = "rating")
	private TaskRating rating=null;
	
	/**
	 * Thông tin thực hiện lại
	 */
	@Field(value = "redo")
	private TaskRedo redo=null;
	
	/**
	 * Danh sách lịch sử thực hiện lại
	 */
	@Field(value = "redoHistories")
	private LinkedList<TaskRedo> redoHistories=new LinkedList<>();
	
	/**
	 * Thông tin từ chối thực hiện
	 */
	@Field(value = "refuse")
	private TaskRefuse refuse=null;
	
	/**
	 * Danh sách lịch sử từ chối thực hiện
	 */
	@Field(value = "refuseHitories")
	private LinkedList<TaskRefuse> refuseHitories=new LinkedList<>();
	
	/**
	 * Thông tin tạm hoãn
	 */
	@Field(value = "pending") 
	private TaskPending pending=null;
	
	/**
	 * Danh sách lịch sử tạm hoãn
	 */
	@Field(value = "pendingHistories")
	private LinkedList<TaskPending> pendingHistories=new LinkedList<>();
	
	/**
	 * Thông tin thông báo
	 */
	@Field(value = "notify")
	private TaskNotify notify=null;
	
	/**
	 * Thông tin văn bản cơ bản
	 */
	@Field(value = "docInfo")
	private TaskDocInfo docInfo=null;
	
	/**
	 * Danh sách văn bản liên quan nhiệm vụ
	 */
	@Field(value = "docReferences")
	private LinkedList<TaskDocReference> docReferences=new LinkedList<>();
	
	/**
	 * Thông tin nhiệm vụ đồng bộ từ ngoài hệ thống
	 */
	@Field(value = "syncSourceExternal")
	private TaskSyncSourceExternal syncSourceExternal=null;
	
	/**
	 * Nhiệm vụ đã xóa
	 */
	@Indexed
	@Field(value = "trash")
	private boolean trash;
	
	/**
	 * Nhiệm vụ cấp dưới báo cáo hoàn thành
	 * thì báo cáo lên cấp trên luôn
	 */
	@Indexed
	@Field(value = "allowDelegate")
	private boolean allowDelegate;
	
	public Task(){
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.trash=false;
	}
	
	public ObjectId getObjectId() {
		return id;
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTimeLong() {
		if(getCreatedTime()!=null) {
			return getCreatedTime().getTime();
		}
		return 0;
	}
	
	public long getUpdatedTimeLong() {
		if(getUpdatedTime()!=null) {
			return getUpdatedTime().getTime();
		}
		return 0;
	}
	
	public long getEndTimeLong() {
		if(getEndTime()!=null) {
			return getEndTime().getTime();
		}
		return 0;
	}
	
	public long getStartingTimeLong() {
		if(getStartingTime()!=null) {
			return getStartingTime().getTime();
		}
		return 0;
	}
	
	public boolean isPending() {
		if(pending!=null)
			return true;
		return false;
	}
	
	public boolean isStarting() {
		if(startingTime!=null)
			return true;
		return false;
	}
	
	public boolean hasEndTime() {
		return endTime!=null;
	}
	
	public boolean isReported() {
		return reported!=null;
	}
	
	public boolean isCompleted() {
		return completed!=null;
	}
	
	public String getStatus() {
		String status="";
		if(state.equalsIgnoreCase(TaskState.dangthuchien.getKey()) || state.equalsIgnoreCase(TaskState.choxacnhan.getKey()) || state.equalsIgnoreCase(TaskState.dahoanthanh.getKey())) {
			if(hasEndTime()) {
				long now=new Date().getTime();
				if(now <= getEndTimeLong()) {
					status="_tronghan";
				}else {
					status="_quahan";
				}
			}else {
				status="_khonghan";
			}
		}
		return state+status;
	}
}

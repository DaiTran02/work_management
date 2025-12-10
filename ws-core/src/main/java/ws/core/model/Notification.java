package ws.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.google.gson.Gson;

import lombok.Data;
import ws.core.enums.NotificationAction;
import ws.core.enums.NotificationObject;
import ws.core.enums.NotificationScope;
import ws.core.enums.NotificationType;
import ws.core.model.embeded.Creator;
import ws.core.model.embeded.Receiver;

@Data
@Document(collection = "notification")
public class Notification {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	/**
	 * Thời gian tạo thông báo, và chỉ giữ trong 90 ngày
	 */
	@Indexed(expireAfter = "90d")
	@Field(value = "createdTime")
	private Date createdTime;
	
	/**
	 * Thời gian cập nhật (nếu có)
	 */
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	/**
	 * Tiêu đề thông báo
	 */
	@Indexed
	@Field(value = "title")
	private String title;
	
	/**
	 * Nội dung thông báo
	 */
	@Field(value = "content")
	private String content;
	
	/**
	 * Loại đối tượng của thông báo như info, warning, error, success
	 */
	@Indexed
	@Field(value = "type")
	private NotificationType type;
	
	/**
	 * Hành động của đối tượng thông báo (nếu có)
	 */
	@Indexed
	@Field(value = "action")
	private NotificationAction action;
	
	/**
	 * URL để thực hiện thành động (nếu có)
	 */
	@Field(value = "actionUrl")
	private String actionUrl;
	
	/**
	 * Loại đối tượng của thông báo như doc, task, event, ...
	 */
	@Indexed
	@Field(value = "object")
	private NotificationObject object;
	
	/**
	 * ID của đối tượng thông báo
	 */
	@Indexed
	@Field(value = "objectId")
	private String objectId;
	
	/**
	 * Thông tin người gửi
	 */
	@Indexed
	@Field(value = "creator")
	private Creator creator;
	
	/**
	 * Thông tin người nhận
	 */
	@Indexed
	@Field(value = "receiver")
	private Receiver receiver;
	
	/**
	 * Người nhận đã xem chưa
	 */
	@Indexed
	@Field(value = "viewed")
	private boolean viewed;
	
	/**
	 * Thời gian đã xem
	 */
	@Field(value = "viewedTime")
	private Date viewedTime;
	
	/**
	 * Pham vi thông báo, ví dụ như đơn vị hay tổ, cá nhân
	 */
	@Indexed
	@Field(value = "scope")
	private NotificationScope scope;
	
	@Field(value = "metaDatas")
	private List<Document> metaDatas=new ArrayList<>();
	
	public Notification() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.viewed=false;
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTimeLong() {
		if(createdTime!=null)
			return createdTime.getTime();
		return 0;
	}
	
	public long getUpdatedTimeLong() {
		if(updatedTime!=null)
			return updatedTime.getTime();
		return 0;
	}
	
	public long getViewedTimeLong() {
		if(viewedTime!=null)
			return viewedTime.getTime();
		return 0;
	}
	
	public Notification clone() {
		Gson gson = new Gson();
		Notification notification = gson.fromJson(gson.toJson(this), Notification.class);
		notification.setId(ObjectId.get());
		return notification;
	}
}

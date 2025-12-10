package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import ws.core.enums.NotificationAction;
import ws.core.enums.NotificationObject;
import ws.core.enums.NotificationScope;
import ws.core.enums.NotificationType;
import ws.core.model.embeded.Creator;
import ws.core.model.embeded.Receiver;

@Data
public class ReqNotificationCreate {
	/**
	 * Tiêu đề thông báo
	 */
	@NotEmpty(message = "title không được rỗng")
	private String title;
	
	/**
	 * Nội dung thông báo
	 */
	@NotEmpty(message = "content không được rỗng")
	private String content;
	
	/**
	 * Loại thông báo: lỗi, thành công, thông tin, cảnh báo, ...
	 */
	@NotEmpty(message = "type không được rỗng")
	private NotificationType type;
	
	/**
	 * Thông báo liên quan hành động nào
	 */
	@NotEmpty(message = "action không được rỗng")
	private NotificationAction action;
	
	/**
	 * Đường dẫn thực hiện hành động (nếu có)
	 */
	private String actionUrl;
	
	/**
	 * Đối tượng liên quan, như task, doc, ...
	 */
	private NotificationObject object;
	
	/**
	 * Id của đối tượng liên quan, như taskId, docId, ...
	 */
	private String objectId;
	
	/**
	 * Thông tin người tạo
	 */
	private Creator creator;
	
	/**
	 * Thông tin người nhận
	 */
	private Receiver receiver;
	
	/**
	 * Phạm vi thông báo, ví dụ như đơn vị hay cá nhân, có thể null
	 */
	private NotificationScope scope;
	
	/**
	 * Các trường dữ liệu mở rộng
	 */
	private List<Document> metaDatas=new ArrayList<>();
	
}

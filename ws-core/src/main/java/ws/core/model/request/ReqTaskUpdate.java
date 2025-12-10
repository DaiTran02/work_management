package ws.core.model.request;

import java.util.LinkedList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.model.request.embeded.ReqTaskAssignee;
import ws.core.model.request.embeded.ReqTaskAssistant;
import ws.core.model.request.embeded.ReqTaskFollower;
import ws.core.model.request.embeded.ReqTaskOwner;
import ws.core.model.request.embeded.ReqTaskSupport;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqTaskUpdate {
	//@NotNull(message = "docId không được trống")
	//@ValidObjectId(message = "docId không hợp lệ")
	public String docId;
	
	@Valid
	@NotNull(message = "owner không được trống")
	public ReqTaskOwner owner;
	
	@Valid
	@NotNull(message = "assistant không được trống")
	public ReqTaskAssistant assistant;
	
	@Valid
	@NotNull(message = "assignee không được trống")
	public ReqTaskAssignee assignee;
	
	@Valid
	@NotNull(message = "supports không được trống")
	public List<ReqTaskSupport> supports;
	
	@Valid
	@NotNull(message = "followers không được trống")
	public List<ReqTaskFollower> followers;
	
	@NotNull(message = "priority không được trống")
	@ValidStringMedium(message = "priority không được chứa các ký tự đặc biệt")
	public String priority;
	
	@NotNull(message = "title không được trống")
//	@ValidStringExtra(message = "title không được chứa các ký tự đặc biệt")
	public String title;
	
	@NotNull(message = "description không được trống")
//	@ValidStringExtra(message = "description không được chứa các ký tự đặc biệt")
	public String description;
	
	@Min(value = 0, message = "endTime tối thiểu là 0 nghĩa là không hạn")
	public long endTime;
	
	@Min(value = 0)
	public long createTime;
	
	public boolean requiredConfirm=false;
	public boolean requiredKpi=false;
	
	public LinkedList<String> attachments=new LinkedList<String>();
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public ReqTaskUpdate() {

	}
}

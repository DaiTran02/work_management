package ws.core.model.request.embeded;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.embeded.TaskOwner;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqTaskOwner {
	@NotNull(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	private String organizationId;
	
	@NotNull(message = "organizationName không được trống")
	@ValidStringMedium(message = "organizationName không được chứa các ký tự đặc biệt")
	private String organizationName;
	
	@NotNull(message = "organizationUserId không được trống")
	@ValidObjectId(message = "organizationUserId không hợp lệ")
	private String organizationUserId;
	
	@NotNull(message = "organizationUserName không được trống")
	@ValidStringMedium(message = "organizationUserName không được chứa các ký tự đặc biệt")
	private String organizationUserName;
	
	public TaskOwner toTaskOwner() {
		TaskOwner data=new TaskOwner();
		data.setOrganizationId(getOrganizationId());
		data.setOrganizationName(getOrganizationName());
		data.setOrganizationUserId(getOrganizationUserId());
		data.setOrganizationUserName(getOrganizationUserName());
		return data;
	}
}

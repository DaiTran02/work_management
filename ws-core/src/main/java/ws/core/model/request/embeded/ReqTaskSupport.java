package ws.core.model.request.embeded;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.embeded.TaskSupport;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqTaskSupport {
	@NotNull(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	private String organizationId;
	
	@NotNull(message = "organizationName không được trống")
	@ValidStringMedium(message = "organizationName không được chứa các ký tự đặc biệt")
	private String organizationName;
	
	@ValidObjectId(message = "organizationUserId không hợp lệ")
	private String organizationUserId;
	
	@ValidStringMedium(message = "organizationUserName không được chứa các ký tự đặc biệt")
	private String organizationUserName;
	
	public TaskSupport toTaskSupport() {
		TaskSupport data=new TaskSupport();
		data.setOrganizationId(getOrganizationId());
		data.setOrganizationName(getOrganizationName());
		data.setOrganizationUserId(getOrganizationUserId());
		data.setOrganizationUserName(getOrganizationUserName());
		return data;
	}
}

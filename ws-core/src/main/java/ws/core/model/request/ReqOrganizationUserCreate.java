package ws.core.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqOrganizationUserCreate {
	@NotNull(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	public String organizationId;
	
	@NotNull(message = "organizationName không được trống")
	@ValidStringMedium(message = "organizationName không được chứa các ký tự đặc biệt")
	public String organizationName;
	
	@NotNull(message = "organizationUserId không được trống")
	@ValidObjectId(message = "organizationUserId không hợp lệ")
	public String organizationUserId;
	
	@NotNull(message = "organizationUserName không được trống")
	@ValidStringMedium(message = "organizationUserName không được chứa các ký tự đặc biệt")
	public String organizationUserName;
}

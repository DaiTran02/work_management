package ws.core.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ws.core.validation.ValidObjectId;

@Data
public class ReqUserLoginAddToOrganization {
	@NotBlank(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	private String organizationId;
	
	@ValidObjectId(message = "groupId không hợp lệ")
	private String groupId;
	
	@ValidObjectId(message = "roleId không hợp lệ")
	private String roleId;
}

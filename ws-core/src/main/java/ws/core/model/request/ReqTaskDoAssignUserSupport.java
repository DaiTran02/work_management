package ws.core.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqTaskDoAssignUserSupport {
	@NotNull(message = "organizationUserId không được trống")
	@ValidObjectId(message = "organizationUserId không hợp lệ")
	public String organizationUserId;
	
	@NotNull(message = "organizationUserName không được trống")
	@ValidStringMedium(message = "organizationUserName không được chứa các ký tự đặc biệt")
	public String organizationUserName;
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public ReqTaskDoAssignUserSupport() {
		
	}
}

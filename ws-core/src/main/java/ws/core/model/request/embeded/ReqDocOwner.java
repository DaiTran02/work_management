package ws.core.model.request.embeded;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqDocOwner {
	@NotNull(message = "owner.organizationId không được trống")
	@ValidObjectId(message = "owner.organizationId không hợp lệ")
	private String organizationId;
	
	@NotNull(message = "owner.organizationName không được trống")
	@ValidStringMedium(message = "owner.organizationName không được chứa các ký tự đặc biệt")
	private String organizationName;
	
	@ValidObjectId(message = "owner.organizationGroupId không hợp lệ")
	private String organizationGroupId;
	
	@ValidStringMedium(message = "owner.organizationGroupName không được chứa các ký tự đặc biệt")
	private String organizationGroupName;
	
	@NotNull(message = "owner.organizationUserId không được trống")
	@ValidObjectId(message = "owner.organizationUserId không hợp lệ")
	private String organizationUserId;
	
	@NotNull(message = "owner.organizationUserName không được trống")
	@ValidStringMedium(message = "owner.organizationUserName không được chứa các ký tự đặc biệt")
	private String organizationUserName;
}

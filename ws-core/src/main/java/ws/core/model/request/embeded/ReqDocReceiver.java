package ws.core.model.request.embeded;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqDocReceiver {
	@NotNull(message = "receiver.organizationId không được trống")
	@ValidObjectId(message = "receiver.organizationId không hợp lệ")
	private String organizationId;
	
	@NotNull(message = "receiver.organizationName không được trống")
	@ValidStringMedium(message = "receiver.organizationName không được chứa các ký tự đặc biệt")
	private String organizationName;
	
	@ValidObjectId(message = "receiver.organizationUserId không hợp lệ")
	private String organizationUserId;
	
	@ValidStringMedium(message = "receiver.organizationUserName không được chứa các ký tự đặc biệt")
	private String organizationUserName;
}

package ws.core.model.request.embeded;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqDocOwnerPartner {
	@NotNull(message = "owner.organizationCode không được trống")
	private String organizationCode;
	
	@NotNull(message = "owner.username không được trống")
	private String username;
}

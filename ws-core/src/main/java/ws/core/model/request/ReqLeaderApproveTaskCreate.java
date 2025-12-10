package ws.core.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqLeaderApproveTaskCreate {
	@NotNull(message = "name không được trống")
	@ValidStringMedium(message = "name không được chứa các ký tự đặc biệt")
	public String name;
	
	@NotNull(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	public String organizationId;
	
	@NotNull(message = "organizationName không được trống")
	@ValidStringMedium(message = "organizationName không được chứa các ký tự đặc biệt")
	public String organizationName;
	
	public int order=1;
	
	public boolean active = true;
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
}

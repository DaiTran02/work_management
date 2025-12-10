package ws.core.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;

@Data
public class ReqTaskDoUnAssignUserAssignee {
	@NotNull(message = "reason không được trống")
//	@ValidStringMedium(message = "reason không được chứa các ký tự đặc biệt")
	public String reason;
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public ReqTaskDoUnAssignUserAssignee() {
		
	}
}

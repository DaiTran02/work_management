package ws.core.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;

@Data
public class ReqTaskDoConfirm {
	public long completedTime=0;
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public ReqTaskDoConfirm() {
		
	}
}

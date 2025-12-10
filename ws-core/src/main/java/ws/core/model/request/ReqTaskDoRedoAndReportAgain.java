package ws.core.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;

@Data
public class ReqTaskDoRedoAndReportAgain {

	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
}

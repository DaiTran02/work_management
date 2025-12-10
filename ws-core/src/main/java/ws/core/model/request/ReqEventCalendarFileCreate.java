package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;

@Data
public class ReqEventCalendarFileCreate {
	@Min(value = 0, message = "time là thời gian mili-seconds không được trống")
	private long time;
	
	private List<String> attachments=new ArrayList<String>();
	
	@Valid
	@NotNull(message = "creator không được trống")
	private ReqCreator creator;
}

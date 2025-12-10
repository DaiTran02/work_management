package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ReqEventCalendarFileUpdate {
	@Min(value = 0, message = "time là thời gian mili-seconds không được trống")
	private long time;
	
	private List<String> attachments=new ArrayList<String>();
}

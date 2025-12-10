package ws.core.model.request.embeded;

import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.embeded.EventCalendarResourceAttach;
import ws.core.validation.ValidObjectId;

@Data
public class ReqEventCalendarResourceAttach {
	@NotNull(message = "resourceId không được trống")
	@ValidObjectId(message = "resourceId không hợp lệ")
	private String resourceId;
	
	private int number;
	
	private String note;
	
	public EventCalendarResourceAttach toEventCalendarResourceAttach() {
		EventCalendarResourceAttach  eventCalendarResourceAttach=new EventCalendarResourceAttach();
		eventCalendarResourceAttach.setUpdatedTime(new Date());
		eventCalendarResourceAttach.setResourceId(getResourceId());
		eventCalendarResourceAttach.setNumber(getNumber());
		eventCalendarResourceAttach.setNote(getNote());
		return eventCalendarResourceAttach;
	}
}

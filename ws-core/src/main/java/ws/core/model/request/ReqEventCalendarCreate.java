package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.enums.EventCalendarPeriod;
import ws.core.enums.EventCalendarType;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.model.request.embeded.ReqEventCalendarResourceAttach;
import ws.core.model.request.embeded.ReqEventCalendarUserAttand;
import ws.core.validation.ValidEnum;

@Data
public class ReqEventCalendarCreate {
	@Min(value = 0)
	private long from;
	
	@Min(value = 0)
	private long to;
	
	@NotNull(message = "type không được trống")
	@ValidEnum(enumClass = EventCalendarType.class, message = "Giá trị không hợp lệ")
	private String type;
	
	@ValidEnum(enumClass = EventCalendarPeriod.class, message = "Giá trị không hợp lệ")
	private String period;
	
	@NotNull(message = "content không được trống")
	private String content;
	
	private String notes;
	
	private String color;
	
	@Valid
	private List<ReqEventCalendarUserAttand> hosts=new ArrayList<ReqEventCalendarUserAttand>();
	
	@Valid
	private List<ReqEventCalendarUserAttand> attendeesRequired=new ArrayList<ReqEventCalendarUserAttand>();
	
	@Valid
	private List<ReqEventCalendarUserAttand> attendeesNoRequired=new ArrayList<ReqEventCalendarUserAttand>();
	
	@Valid
	private List<ReqEventCalendarUserAttand> prepareres=new ArrayList<ReqEventCalendarUserAttand>();
	
	@Valid
	private List<ReqEventCalendarResourceAttach> resources=new ArrayList<ReqEventCalendarResourceAttach>();
	
	private List<String> attachments=new ArrayList<String>();
	
	@Valid
	@NotNull(message = "creator không được trống")
	private ReqCreator creator;
}

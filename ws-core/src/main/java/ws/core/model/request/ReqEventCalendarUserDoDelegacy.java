package ws.core.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.enums.EventCalendarUserType;
import ws.core.model.request.embeded.ReqEventCalendarUserAttand;
import ws.core.validation.ValidEnum;

@Data
public class ReqEventCalendarUserDoDelegacy {
	@NotNull(message = "type không được trống")
	@ValidEnum(enumClass = EventCalendarUserType.class, message = "type không hợp lệ")
	private String type;
	
	@Valid
	@NotNull(message = "fromUser không được trống")
	private ReqEventCalendarUserAttand fromUser;
	
	@Valid
	@NotNull(message = "toUser không được trống")
	private ReqEventCalendarUserAttand toUser;
}

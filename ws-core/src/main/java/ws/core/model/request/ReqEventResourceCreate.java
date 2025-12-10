package ws.core.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.enums.EventCalendarType;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.validation.ValidEnum;

@Data
public class ReqEventResourceCreate {
	@NotNull(message = "type không được trống")
	@ValidEnum(enumClass = EventCalendarType.class, message = "Giá trị không hợp lệ")
	private String type;
	
	@NotNull(message = "name không được trống")
	private String name;
	
	private String description;
	
	@Min(value = 0, message = "group chỉ được phép là 0 hoặc 1")
	@Max(value = 2, message = "group chỉ được phép là 0 hoặc 1")
	private int group;
	
	@Valid
	@NotNull(message = "creator không được trống")
	private ReqCreator creator;
}

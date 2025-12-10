package ws.core.model.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.enums.EventCalendarUserStatus;
import ws.core.validation.ValidEnum;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqEventCalendarUserDoConfirmed {
	@NotNull(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	private String organizationId;
	
	@NotNull(message = "organizationName không được trống")
	private String organizationName;
	
	@NotNull(message = "organizationUserId không được trống")
	@ValidObjectId(message = "organizationUserId không hợp lệ")
	private String organizationUserId;
	
	@NotNull(message = "organizationUserName không được trống")
	private String organizationUserName;
	
	@NotNull(message = "status không được trống")
	@ValidEnum(enumClass = EventCalendarUserStatus.class, message = "status không hợp lệ")
	private String status;
	
	@ValidStringMedium(message = "notes không được chứa các ký tự đặc biệt")
	private String notes;
}

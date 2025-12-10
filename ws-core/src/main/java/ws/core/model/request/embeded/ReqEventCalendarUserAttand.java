package ws.core.model.request.embeded;

import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.enums.EventCalendarAttandUserOrganizationType;
import ws.core.enums.EventCalendarUserStatus;
import ws.core.model.embeded.EventCalendarUserAttend;
import ws.core.validation.ValidEnum;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqEventCalendarUserAttand implements Cloneable{
	@NotNull(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	private String organizationId;
	
	@NotNull(message = "organizationName không được trống")
	@ValidStringMedium(message = "organizationName không được chứa các ký tự đặc biệt")
	private String organizationName;
	
	@ValidObjectId(message = "organizationUserId không hợp lệ")
	private String organizationUserId;
	
	@ValidStringMedium(message = "organizationUserName không được chứa các ký tự đặc biệt")
	private String organizationUserName;
	
	@ValidStringMedium(message = "jobTitle không được chứa các ký tự đặc biệt")
	private String jobTitle;
	
	@ValidEnum(enumClass = EventCalendarUserStatus.class, message = "status không hợp lệ")
	private String status=EventCalendarUserStatus.unconfirm.getKey();
	
	@ValidStringMedium(message = "notes không được chứa các ký tự đặc biệt")
	private String notes;
	
	private String type=EventCalendarAttandUserOrganizationType.organization.getKey();

	public EventCalendarUserAttend toUserOrganizationEventCalendar() {
		EventCalendarUserAttend userOrganizationEventCalendar=new EventCalendarUserAttend();
		userOrganizationEventCalendar.setOrganizationId(getOrganizationId());
		userOrganizationEventCalendar.setOrganizationName(getOrganizationName());
		userOrganizationEventCalendar.setOrganizationUserId(getOrganizationUserId());
		userOrganizationEventCalendar.setOrganizationUserName(getOrganizationUserName());
		userOrganizationEventCalendar.setJobTitle(getJobTitle());
		userOrganizationEventCalendar.setStatus(getStatus());
		userOrganizationEventCalendar.setNotes(getNotes());
		userOrganizationEventCalendar.setType(getType());
		if(getStatus()!=null && !getStatus().equalsIgnoreCase(EventCalendarUserStatus.unconfirm.getKey())) {
			userOrganizationEventCalendar.setConfirmedTime(new Date());
		}
		return userOrganizationEventCalendar;
	}
	
	public boolean isUser() {
		if(getOrganizationId()!=null && getOrganizationName()!=null && getOrganizationUserId()!=null && getOrganizationUserName()!=null) {
			return true;
		}
		return false;
	}
	
	public boolean isSimilar(ReqEventCalendarUserAttand reqEventCalendarUserAttend) {
		return getOrganizationId().equalsIgnoreCase(reqEventCalendarUserAttend.getOrganizationId()) && getOrganizationUserId().equalsIgnoreCase(reqEventCalendarUserAttend.getOrganizationUserId());
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

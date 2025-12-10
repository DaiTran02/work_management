package ws.core.model.embeded;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class CreatorInfo{
	@Field(value = "organizationId")
	private String organizationId;
	
	@Field(value = "organizationName")
	private String organizationName;
	
	@Field(value = "organizationUserId")
	private String organizationUserId;
	
	@Field(value = "organizationUserName")
	private String organizationUserName;
	
	@Field(value = "jobTitle")
	private String jobTitle;
	
	public String getTextDisplay() {
		String text=organizationName;
		if(text!=null && organizationUserName!=null) {
			text+=" ("+organizationUserName+")";
		}
		return text;
	}
	
	public EventCalendarUserAttend toEventCalendarUserAttend() {
		EventCalendarUserAttend eventCalendarUserAttend=new EventCalendarUserAttend();
		eventCalendarUserAttend.setOrganizationId(organizationId);
		eventCalendarUserAttend.setOrganizationName(organizationName);
		eventCalendarUserAttend.setOrganizationUserId(organizationUserId);
		eventCalendarUserAttend.setOrganizationUserName(organizationUserName);
		eventCalendarUserAttend.setJobTitle(jobTitle);
		return eventCalendarUserAttend;
	}
}

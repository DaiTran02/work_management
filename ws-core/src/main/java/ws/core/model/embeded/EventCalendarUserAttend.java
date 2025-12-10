package ws.core.model.embeded;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.enums.EventCalendarAttandUserOrganizationType;
import ws.core.model.request.ReqEventCalendarUserDoConfirmed;

@Data
public class EventCalendarUserAttend {
	@Field(value = "type")
	private String type;
	
	@Indexed
	@Field(value = "organizationId")
	private String organizationId;
	
	@Field(value = "organizationName")
	private String organizationName;
	
	@Indexed
	@Field(value = "organizationUserId")
	private String organizationUserId;
	
	@Field(value = "organizationUserName")
	private String organizationUserName;
	
	@Field(value = "jobTitle")
	private String jobTitle;
	
	@Indexed
	@Field(value = "status")
	private String status;
	
	@Field(value = "notes")
	private String notes;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "confirmedTime")
	private Date confirmedTime;
	
	@Field(value = "delegacyTime")
	private Date delegacyTime;
	
	@Field(value = "historiesDelegacy")
	private List<EventCalendarUserAttend> historiesDelegacy=new ArrayList<EventCalendarUserAttend>();
	
	public EventCalendarUserAttend() {
		this.type=EventCalendarAttandUserOrganizationType.user.getKey();
		this.createdTime=new Date();
	}
	
	public long getCreatedTime() {
		if(createdTime!=null)
			return createdTime.getTime();
		return 0;
	}
	
	public long getConfirmedTime() {
		if(confirmedTime!=null)
			return confirmedTime.getTime();
		return 0;
	}
	
	public long getDelegacyTime() {
		if(delegacyTime!=null)
			return delegacyTime.getTime();
		return 0;
	}
	
	public boolean isSimilar(EventCalendarUserAttend eventCalendarUserAttand) {
		return getOrganizationId().equals(eventCalendarUserAttand.getOrganizationId()) && getOrganizationUserId().equals(eventCalendarUserAttand.getOrganizationUserId());
	}
	
	public boolean isSimilar(ReqEventCalendarUserDoConfirmed reqEventCalendarUserDoConfirmed) {
		return getOrganizationId().equals(reqEventCalendarUserDoConfirmed.getOrganizationId()) && getOrganizationUserId().equals(reqEventCalendarUserDoConfirmed.getOrganizationUserId());
	}
	
	public Creator toCreator() {
		Creator creator=new Creator();
		creator.setOrganizationId(getOrganizationId());
		creator.setOrganizationName(getOrganizationName());
		creator.setOrganizationUserId(getOrganizationUserId());
		creator.setOrganizationUserName(getOrganizationUserName());
		return creator;
	}
	
	public Receiver toReceiver() {
		Receiver receiver=new Receiver();
		receiver.setOrganizationId(getOrganizationId());
		receiver.setOrganizationName(getOrganizationName());
		receiver.setOrganizationUserId(getOrganizationUserId());
		receiver.setOrganizationUserName(getOrganizationUserName());
		return receiver;
	}
}

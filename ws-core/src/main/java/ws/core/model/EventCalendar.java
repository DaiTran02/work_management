package ws.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.enums.EventCalendarPeriod;
import ws.core.model.embeded.Actor;
import ws.core.model.embeded.Creator;
import ws.core.model.embeded.EventCalendarResourceAttach;
import ws.core.model.embeded.EventCalendarUserAttend;
import ws.core.util.DateTimeUtil;

@Data
@Document(collection = "event_calendar")
public class EventCalendar {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Indexed
	@Field(value="type")
	private String type;
	
	@Indexed
	@Field(value="period")
	private String period;
	
	@Indexed(direction = IndexDirection.ASCENDING)
	@Field(value="from")
	private Date from;
	
	@Indexed(direction = IndexDirection.ASCENDING)
	@Field(value="to")
	private Date to;
	
	@TextIndexed
	@Field(value="content")
	private String content;
	
	@TextIndexed
	@Field(value="notes")
	private String notes;
	
	@Field(value="color")
	private String color;
	
	@Indexed
	@Field(value = "hosts")
	private List<EventCalendarUserAttend> hosts=new ArrayList<EventCalendarUserAttend>();
	
	@Indexed
	@Field(value = "attendeesRequired")
	private List<EventCalendarUserAttend> attendeesRequired=new ArrayList<EventCalendarUserAttend>();
	
	@Indexed
	@Field(value = "attendeesNoRequired")
	private List<EventCalendarUserAttend> attendeesNoRequired=new ArrayList<EventCalendarUserAttend>();
	
	@Indexed
	@Field(value = "prepareres")
	private List<EventCalendarUserAttend> prepareres=new ArrayList<EventCalendarUserAttend>();
	
	@Indexed
	@Field(value = "resources")
	private List<EventCalendarResourceAttach> resources=new ArrayList<EventCalendarResourceAttach>();
	
	@Indexed
	@Field(value = "attachments")
	private List<String> attachments=new ArrayList<String>();
	
	@Field(value = "creator")
	private Creator creator;
	
	@Field(value = "actor")
	private Actor actor;
	
	@Indexed
	@Field(value="trash")
	private boolean trash;
	
	@Indexed
	@Field(value="notifyBeforeEvent")
	private boolean notifyBeforeEvent;
	
	public EventCalendar() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.trash=false;
		this.notifyBeforeEvent=false;
	}
	
	public ObjectId getObjectId() {
		return id;
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTime() {
		return this.createdTime.getTime();
	}
	
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
	
	public long getFromLong() {
		return this.from.getTime();
	}
	
	public long getToLong() {
		return this.to.getTime();
	}
	
	public boolean canDelete() {
		if(isTrash()==false) {
			return true;
		}
		return false;
	}
	
	public boolean canUpdate() {
		if(isTrash()==false) {
			return true;
		}
		return false;
	}
	
	public String getSearching() {
		return String.join(" ", content, notes);
	}
	
	public List<EventCalendarUserAttend> getAllUsers(){
		List<EventCalendarUserAttend> allUsers=new ArrayList<EventCalendarUserAttend>();
		allUsers.addAll(getHosts());
		allUsers.addAll(getAttendeesRequired());
		allUsers.addAll(getAttendeesNoRequired());
		return allUsers;
	}
	
	public boolean isCanSeparate() {
		if((getPeriod()!=null && getPeriod().equals(EventCalendarPeriod.days.getName())) 
				|| (getToLong()>0 && DateTimeUtil.getDifferenceDays(getFrom(), getTo()) > 1)) {
			return true;
		}
		return false;
	}
}

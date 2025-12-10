package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "event_history")
public class EventHistory {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Field(value = "creatorId")
	private String creatorId;
	
	@Field(value = "creatorName")
	private String creatorName;
	
	@Indexed
	@Field(value = "eventId")
	private String eventId;
	
	@Field(value = "data")
	private EventCalendar data;
	
	public EventHistory() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
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
}

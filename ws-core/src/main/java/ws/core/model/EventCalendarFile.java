package ws.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.model.embeded.Creator;

@Data
@Document(collection = "event_calendar_file")
public class EventCalendarFile {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Indexed(direction = IndexDirection.ASCENDING)
	@Field(value="time")
	private Date time;
	
	@Indexed
	@Field(value = "attachments")
	private List<String> attachments=new ArrayList<String>();
	
	@Field(value = "creator")
	private Creator creator;
	
	@Indexed
	@Field(value="trash")
	private boolean trash;
	
	public EventCalendarFile() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.trash=false;
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
	
	public long getTimeLong() {
		return this.time.getTime();
	}
	
	public long getToLong() {
		return this.time.getTime();
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
}

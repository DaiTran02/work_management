package ws.core.model.embeded;

import java.util.Date;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskComment {
	@Field(value = "_id")
	private ObjectId id;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Field(value = "message")
	private String message;
	
	@Field(value = "creator")
	private CreatorInfo creator;
	
	@Field(value = "attachments")
	private LinkedList<String> attachments=new LinkedList<>();
	
	@Field(value="replies")
	public LinkedList<TaskComment> replies=new LinkedList<>();
	
	public TaskComment() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
	}
	
	public ObjectId getObjectId() {
		return id;
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTimeLong() {
		if(createdTime!=null) {
			return createdTime.getTime();
		}
		return 0;
	}
	
	public long getUpdatedTimeLong() {
		if(updatedTime!=null) {
			return updatedTime.getTime();
		}
		return 0;
	}
}

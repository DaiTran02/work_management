package ws.core.model.embeded;

import java.util.Date;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskRemind {
	@Field(value = "_id")
	private ObjectId id;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "reasonRemind")
	private String reasonRemind;
	
	@Field(value = "creator")
	private CreatorInfo creator;
	
	@Field(value = "attachments")
	private LinkedList<String> attachments=new LinkedList<>();
	
	public TaskRemind() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTime() {
		if(createdTime!=null) {
			return createdTime.getTime();
		}
		return 0;
	}
}

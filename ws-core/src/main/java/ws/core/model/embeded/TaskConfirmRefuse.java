package ws.core.model.embeded;

import java.util.Date;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskConfirmRefuse {
	@Field(value = "_id")
	private ObjectId id;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "reasonConfirmRefuse")
	private String reasonConfirmRefuse;
	
	@Field(value = "attachments")
	private LinkedList<String> attachments=new LinkedList<>();
	
	@Field(value = "creator")
	private CreatorInfo creator;
	
	public TaskConfirmRefuse() {
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

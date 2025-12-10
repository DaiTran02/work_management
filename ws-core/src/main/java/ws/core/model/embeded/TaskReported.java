package ws.core.model.embeded;

import java.util.Date;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskReported {
	@Field(value = "_id")
	private ObjectId id;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Indexed
	@Field(value = "completedTime")
	private Date completedTime;
	
	@Indexed
	@Field(value = "reportedStatus")
	private String reportedStatus;
	
	@Field(value = "attachments")
	private LinkedList<String> attachments=new LinkedList<>();
	
	@Field(value = "creator")
	private CreatorInfo creator;
	
	public TaskReported(){
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
	
	public long getCompletedTime() {
		if(completedTime!=null) {
			return completedTime.getTime();
		}
		return 0;
	}
}

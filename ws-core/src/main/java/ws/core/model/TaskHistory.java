package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.model.embeded.Creator;

@Data
@Document(collection = "task_history")
public class TaskHistory {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Indexed
	@Field(value = "taskId")
	private String taskId;
	
	@Field(value = "taskData")
	private Object taskData;
	
	@Field(value = "action")
	private String action;
	
	@Field(value = "creator")
	private Creator creator;
	
	public TaskHistory() {
		id=ObjectId.get();
		createdTime=new Date();
		updatedTime=new Date();
	}
}

package ws.core.model.embeded;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Data;

@Data
public class TaskRating {
	@Field(value = "_id")
	private ObjectId id;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Indexed
	@Field(value = "star")
	private int star;
	
	@Field(value = "maskA")
	private Double maskA;
	
	@Field(value = "maskB")
	private Double maskB;
	
	@Field(value = "maskC")
	private Double maskC;
	
	@Field(value = "totalPercent")
	private Double totalPercent;
	
	@Field(value = "totalMark")
	private Double totalMark;
	
	@Field(value = "explain")
	private String explain;
	
	@Field(value = "creator")
	private CreatorInfo creator;
	
	public TaskRating() {
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

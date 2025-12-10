package ws.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.model.embeded.Creator;

@Data
@Document(collection = "tag")
public class Tag {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@TextIndexed
	@Field(value = "name")
	private String name;
	
	@Field(value = "color")
	private String color;
	
	@Indexed
	@Field(value = "creator")
	private Creator creator;
	
	@Indexed
	@Field(value = "type")
	private String type;
	
	@Indexed
	@Field(value = "classIds")
	private List<String> classIds=new ArrayList<>();
	
	@Indexed
	@Field(value = "active")
	private boolean active;
	
	@Indexed
	@Field(value = "archive")
	private boolean archive;
	
	public Tag() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.active=true;
		this.archive=false;
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

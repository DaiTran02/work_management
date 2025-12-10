package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.model.embeded.Actor;
import ws.core.model.embeded.Creator;

@Data
@Document(collection = "event_resource")
public class EventResource {
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
	
	@TextIndexed
	@Field(value="name")
	private String name;
	
	@TextIndexed
	@Field(value="description")
	private String description;
	
	@Indexed
	@Field(value="group")
	private int group;
	
	@Indexed
	@Field(value = "creator")
	private Creator creator;
	
	@Field(value="actor")
	private Actor actor;
	
	@Indexed
	@Field(value="trash")
	private boolean trash;
	
	public EventResource() {
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

package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "configuration")
public class Configuration {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Indexed
	@Field(value = "object")
	private String object;
	
	@Indexed(unique = true)
	@Field(value = "key")
	private String key;
	
	@TextIndexed
	@Field(value = "name")
	private String name;
	
	@TextIndexed
	@Field(value = "description")
	private String description;
	
	@Field(value = "type")
	private String type;
	
	@Field(value = "value")
	private String value;
	
	@Field(value = "active")
	private boolean active;
	
	@Field(value = "orderNumber")
	private int orderNumber;
	
	public Configuration() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.active=true;
		this.orderNumber=1;
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTimeLong() {
		return this.createdTime.getTime();
	}
	
	public long getUpdatedTimeLong() {
		return this.updatedTime.getTime();
	}
}

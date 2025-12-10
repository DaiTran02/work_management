package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "organization_category")
public class OrganizationCategory {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Indexed
	@Field(value = "name")
	private String name;
	
	@Indexed
	@Field(value = "description")
	private String description;
	
	@Indexed
	@Field(value="order")
	private int order;
	
	@Indexed
	@Field(value="active")
	private boolean active;
	
	public OrganizationCategory() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.active=true;
	}
	
	public ObjectId getObjectId() {
		return id;
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTimeLong() {
		if(getCreatedTime()!=null) {
			return getCreatedTime().getTime();
		}
		return 0;
	}
	
	public long getUpdatedTimeLong() {
		if(getUpdatedTime()!=null) {
			return getUpdatedTime().getTime();
		}
		return 0;
	}
}

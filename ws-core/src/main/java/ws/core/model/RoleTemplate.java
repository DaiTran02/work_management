package ws.core.model;

import java.util.Date;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "role_template")
public class RoleTemplate {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Indexed
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Indexed
	@Field(value = "name")
	private String name;
	
	@Indexed
	@Field(value = "description")
	private String description;
	
	@Indexed
	@Field(value = "creatorId")
	private String creatorId;
	
	@Field(value="creatorName")
	private String creatorName;
	
	@Indexed
	@Field(value="permissionKeys")
	private LinkedList<String> permissionKeys;
	
	@Indexed
	@Field(value = "active")
	private boolean active = true;
	
	public RoleTemplate() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.permissionKeys=new LinkedList<String>();
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

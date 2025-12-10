package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "app_mobi")
public class AppMobi {
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Indexed
	@Field(value="userId")
	public String userId;
	
	@Indexed
	@Field(value="deviceId")
	public String deviceId;
	
	@Indexed
	@Field(value="username")
	public String username;
	
	@Indexed
	@Field(value="fullName")
	public String fullName;
	
	@Indexed
	@Field(value="deviceName")
	public String deviceName;
	
	@Indexed
	@Field(value="longitute")
	public String longitute;
	
	@Indexed
	@Field(value = "lagitute")
	public String lagitute;
	
	@Indexed
	@Field(value = "active")
	public boolean active;
	
	public AppMobi() {
		this.id=new ObjectId();
		this.createdTime=new Date();
		this.active=false;
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

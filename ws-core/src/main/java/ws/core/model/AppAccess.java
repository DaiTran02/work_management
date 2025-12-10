package ws.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection =  "app_access")
public class AppAccess {
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime;
	
	@Field(value = "updatedTime")
	public Date updatedTime;
	
	@Indexed
	@Field(value = "creatorId")
	public String creatorId;
	
	@Field(value="creatorName")
	public String creatorName;
	
	@Indexed(unique = true)
	@Field(value="apiKey")
	public String apiKey;
	
	@Indexed
	@Field(value = "startTime")
	public Date startTime;
	
	@Indexed
	@Field(value = "endTime")
	public Date endTime;
	
	@Indexed
	@Field(value="name")
	public String name;
	
	@Indexed
	@Field(value="description")
	public String description;
	
	@Indexed
	@Field(value="organizationId")
	public String organizationId;
	
	@Indexed
	@Field(value="ipsAccess")
	public List<String> ipsAccess=new ArrayList<String>();
	
	@Indexed
	@Field(value="active")
	public boolean active;
	
	public AppAccess(){
		this.id=new ObjectId();
		this.createdTime=new Date();
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
	
	public long getStartTimeLong() {
		if(getStartTime()!=null) {
			return getStartTime().getTime();
		}
		return 0;
	}
	
	public long getEndTimeLong() {
		if(getEndTime()!=null) {
			return getEndTime().getTime();
		}
		return 0;
	}
}

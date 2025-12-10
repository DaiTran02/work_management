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
@Document(collection = "log_access")
public class LogAccess {
	@Id
	@Field(value = "_id")
	public ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	public Date createdTime; 
	
	@Indexed
	@Field(value="userId")
	public String userId;
	
	@Field(value="username")
	public String username;
	
	@Field(value="fullName")
	public String fullName;
	
	@Field(value="userAgent")
	public String userAgent;
	
	@Field(value="ipAddress")
	public String ipAddress;
	
	@Indexed
	@Field(value = "organizationIds")
	public List<String> organizationIds = new ArrayList<String>();
	
	@Field(value = "source")
	public Source source;

	public LogAccess() {
		this.createdTime=new Date();
		this.id=ObjectId.get();
	}
	
	public enum Source{
		browser, application;
	}
}

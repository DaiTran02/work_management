package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "log_request")
public class LogRequest {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime; 
	
	@Field(value = "addremote")
	private String addremote;
	
	@Field(value = "requestURL")
	private String requestURL;
	
	@Field(value = "method")
	private String method;
	
	@Field(value="protocol")
	private String protocol;
	
	@Field(value="query")
	private String query;
	
	@Field(value="body")
	private Object body;
	
	@Field(value="organizationId")
	private String organizationId;
	
	@Field(value="userId")
	private String userId;
	
	@Field(value="userAgent")
	private String userAgent;
	
	public LogRequest() {
		this.id=new ObjectId();
		this.createdTime=new Date();
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
}

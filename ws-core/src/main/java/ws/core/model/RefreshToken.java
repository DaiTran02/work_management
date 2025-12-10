package ws.core.model;


import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection =  "refresh_token")
public class RefreshToken {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Indexed
	@Field(value = "userId")
	private String userId;
	
	@Indexed(unique = true)
	@Field(value="refreshToken")
	private String refreshToken;
	
	@Field(value = "expiryTime")
	private Date expiryTime;
	
	public RefreshToken(){
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
	
	public long getExpiryTimeLong() {
		if(getExpiryTime()!=null) {
			return getExpiryTime().getTime();
		}
		return 0;
	}
}


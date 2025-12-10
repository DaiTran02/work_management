package ws.core.model.embeded;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskDocReference {
	@Field(value = "docId")
	private String docId;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "organizationId")
	private String organizationId;
	
	@Field(value = "organizationName")
	private String organizationName;
	
	@Field(value = "organizationUserId")
	private String organizationUserId;
	
	@Field(value = "organizationUserName")
	private String organizationUserName;
	
	public TaskDocReference() {
		
	}
	
	public long getCreatedTime() {
		if(createdTime!=null) {
			return createdTime.getTime();
		}
		return 0;
	}
}

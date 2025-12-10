package ws.core.model.embeded;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskSyncSourceExternal {
	@Field(value = "syncId")
	private String syncId;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Field(value = "data")
	private String data;
	
	public TaskSyncSourceExternal() {
		
	}
}

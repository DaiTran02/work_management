package ws.core.model.embeded;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class EventCalendarResourceAttach {
	@Indexed
	@Field(value = "resourceId")
	private String resourceId;
	
	@Field(value = "number")
	private int number;
	
	@Field(value = "note")
	private String note;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	public long getUpdatedTime() {
		return this.updatedTime.getTime();
	}
}

package ws.core.model.embeded;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskNotify {
	@Indexed
	@Field(value = "soonExpire")
	private Date soonExpire;
	
	@Indexed
	@Field(value = "hadExpired")
	private Date hadExpired;
	
	public TaskNotify() {
		
	}
	
	public long getSoonExpire() {
		if(soonExpire!=null) {
			return soonExpire.getTime();
		}
		return 0;
	}
	
	public long getHadExpired() {
		if(hadExpired!=null) {
			return hadExpired.getTime();
		}
		return 0;
	}
}

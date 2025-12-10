package ws.core.model.data;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class UserCodePublic implements Serializable{
	private String username;
	private String token;
	private Date expired;
	
	public long getExpiredLong() {
		if(getExpired()!=null) {
			return getExpired().getTime();
		}
		return 0;
	}
	
	public boolean hasExpired() {
		if(getExpired()!=null) {
			if(getExpiredLong() > new Date().getTime()) {
				return false;
			}
		}
		return true;
	}
}

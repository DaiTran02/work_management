package ws.core.model.embeded;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskSupport {
	@Indexed
	@Field(value = "organizationId")
	public String organizationId;
	
	@Field(value = "organizationName")
	public String organizationName;
	
	@Indexed
	@Field(value = "organizationUserId")
	public String organizationUserId;
	
	@Field(value = "organizationUserName")
	public String organizationUserName;
}

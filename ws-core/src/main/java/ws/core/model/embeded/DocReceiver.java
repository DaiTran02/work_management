package ws.core.model.embeded;

import java.io.Serializable;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class DocReceiver implements Serializable{
	@Indexed
	@Field(value = "organizationId")
	private String organizationId;
	
	@Indexed
	@Field(value = "organizationCode")
	private String organizationCode;
	
	@Field(value = "organizationName")
	private String organizationName;
	
	@Indexed
	@Field(value = "organizationUserId")
	private String organizationUserId;
	
	@Field(value = "organizationUserName")
	private String organizationUserName;
}

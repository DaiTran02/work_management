package ws.core.model.embeded;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskAssistant{
	@Indexed
	@Field(value = "organizationId")
	private String organizationId;
	
	@Indexed
	@Field(value = "organizationName")
	private String organizationName;
	
	@Indexed
	@Field(value = "organizationGroupId")
	private String organizationGroupId;
	
	@Indexed
	@Field(value = "organizationGroupName")
	private String organizationGroupName;
	
	@Indexed
	@Field(value = "organizationUserId")
	private String organizationUserId;
	
	@Indexed
	@Field(value = "organizationUserName")
	private String organizationUserName;
	
	public String getTextDisplay() {
		String text=organizationName;
		if(text!=null && organizationUserName!=null) {
			text+=" ("+organizationUserName+")";
		}
		return text;
	}
}

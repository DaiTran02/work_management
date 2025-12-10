package ws.core.model.abstracts;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public abstract class OrganizationUser {
	@Field(value = "organizationId")
	private String organizationId;
	
	@Field(value = "organizationName")
	private String organizationName;
	
	@Field(value = "organizationUserId")
	private String organizationUserId;
	
	@Field(value = "organizationUserName")
	private String organizationUserName;
	
	public String getTextDisplay() {
		String text=organizationName;
		if(text!=null && organizationUserName!=null) {
			text+=" ("+organizationUserName+")";
		}
		return text;
	};
}

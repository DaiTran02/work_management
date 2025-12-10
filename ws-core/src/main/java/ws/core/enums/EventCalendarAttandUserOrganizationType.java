package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EventCalendarAttandUserOrganizationType {
	user("user", "Cá nhân"),
	organization("organization", "Đơn vị");
	
	private String key;
	private String name;
	
	EventCalendarAttandUserOrganizationType(String key, String name){
		this.key=key;
		this.name=name;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}
}

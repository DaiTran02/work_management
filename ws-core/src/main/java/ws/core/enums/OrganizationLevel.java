package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OrganizationLevel {
	organization("organization", "Cấp cơ quan"),
	room("room", "Cấp phòng ban");
	
	private String key;
	private String name;
	
	OrganizationLevel(String key, String name){
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

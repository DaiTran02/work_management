package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EventCalendarType {
	organization("organization", "Đơn vị"),
	personal("personal", "Cá nhân");
	
	private String key;
	private String name;
	
	EventCalendarType(String key, String name){
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

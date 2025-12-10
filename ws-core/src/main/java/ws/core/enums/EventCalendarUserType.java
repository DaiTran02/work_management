package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EventCalendarUserType {
	hosts("hosts", "Chủ trì"),
	attendeesRequired("attendeesRequired", "Người tham gia bắt buộc"),
	attendeesNoRequired("attendeesNoRequired", "Người tham gia không bắt buộc");
	
	private String key;
	private String name;
	
	EventCalendarUserType(String key, String name){
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

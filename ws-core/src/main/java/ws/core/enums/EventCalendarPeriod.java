package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EventCalendarPeriod {
	day("day", "Day"),
	days("days", "Days");
	
	private String key;
	private String name;
	
	EventCalendarPeriod(String key, String name){
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

package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NotificationObject {
	doc("doc","Văn bản"),
	task("task","Nhiệm vụ"),
	user("user","Người dùng"),
	organization("organization","Đơn vị"), 
	system("system","Hệ thống"),
	eventCalendar("eventCalendar","Lịch công tác");
	
	private String key;
	private String name;
	
	NotificationObject(String key, String name){
		this.key=key;
		this.name=name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

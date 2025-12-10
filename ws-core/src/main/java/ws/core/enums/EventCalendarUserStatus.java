package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EventCalendarUserStatus {
	unconfirm("unconfirm", "Chưa xác nhận"),
	accepted("accepted", "Tham gia"),
	denied("denied", "Từ chối tham gia"),
	delegacy("delegacy", "Ủy quyền tham gia");
	
	private String key;
	private String name;
	
	EventCalendarUserStatus(String key, String name){
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

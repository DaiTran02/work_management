package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PermissionKey {
	quanlylichcongtac("quanlylichcongtac", "Quản lý lịch công tác"),
	xemlichcongtacdacbiet("xemlichcongtacdacbiet", "Xem lịch công tác đặc biệt"),
	daidienlichcongtacdonvi("daidienlichcongtacdonvi", "Đại diện lịch công tác khi khi mời Đơn vị");
	
	private String key;
	private String name;
	
	PermissionKey(String key, String name){
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

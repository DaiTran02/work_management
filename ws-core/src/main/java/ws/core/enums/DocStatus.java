package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DocStatus {
	dangthuchien("dangthuchien", "Đang thực hiện"), 
	chuagiaonhiemvu("chuagiaonhiemvu", "Chưa giao nhiệm vụ"),
	vanbandahoanthanh("vanbandahoanthanh", "Văn bản đã hoàn thành");
	
	private String key;
	private String name;
	
	DocStatus(String key, String name){
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

package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DocAction {
	taomoi("taomoi","Tạo mới"), 
	capnhat("capnhat", "Cập nhật"),
	xoa("xoa", "Xóa");
	
	private String key;
	private String name;
	
	DocAction(String key, String name){
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

package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskState {
	tamhoan("tamhoan", "Tạm hoãn", "TH"),
	chuathuchien("chuathuchien", "Chưa thực hiện", "CTH"), 
	tuchoithuchien("tuchoithuchien", "Từ chối thực hiện", "TCTH"),
	dangthuchien("dangthuchien", "Đang thực hiện", "DTH"),
	thuchienlai("thuchienlai", "Thực hiện lại", "THL"),
	choxacnhan("choxacnhan", "Chờ xác nhận", "CXN"),
	tuchoixacnhan("tuchoixacnhan", "Từ chối xác nhận", "TCXN"),
	dahoanthanh("dahoanthanh", "Đã hoàn thành", "DHT");
	
	private String key;
	private String name;
	private String shortName;
	
	TaskState(String key, String name, String shortName){
		this.key=key;
		this.name=name;
		this.shortName=shortName;
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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
}

package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskFilterObject {
	has_rating("has_rating","Nhiệm vụ đã đánh giá"),
	wait_rating("wait_rating","Nhiệm vụ chờ đánh giá"), 
	not_rating("not_rating","Nhiệm vụ chưa đánh giá"), 
	
	has_required_confirm("has_required_confirm","Nhiệm vụ cần xác nhận hoàn thành"), 
	not_required_confirm("not_required_confirm","Nhiệm vụ không cần xác nhận hoàn thành"),
	
	has_endtime("has_endtime","Nhiệm vụ có hạn xử lý"), 
	not_endtime("not_endtime","Nhiệm vụ không hạn xử lý");
	
	private String key;
	private String name;
	
	TaskFilterObject(String key, String name){
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

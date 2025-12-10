package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskEventAction {
	taonhiemvu("taonhiemvu","Tạo nhiệm vụ"), 
	capnhatnhiemvu("capnhatnhiemvu", "Cập nhật nhiệm vụ"),
	tuchoithuchien("tuchoithuchien","Từ chối thực hiện"),
	thuchiennhiemvu("thuchiennhiemvu","Thực hiện nhiệm vụ"),
	hoanthanhnhiemvu("hoanthanhnhiemvu","Hoàn thành nhiệm vụ"),
	trieuhoinhiemvu("trieuhoinhiemvu","Triệu hồi nhiệm vụ"),
	xacnhanhoanthanh("xacnhanhoanthanh","Xác nhận hoàn thành"),
	tuchoixacnhan("tuchoixacnhan","Từ chối xác nhận"),
	thuchienvabaocaolai("thuchienvabaocaolai","Thực hiện và báo cáo lại nhiệm vụ"),
	tamhoanthuchien("tamhoanthuchien","Tạm hoãn thực hiện"),
	tieptucthuchien("tieptucthuchien","Tiếp tục thực hiện"),
	thuchienlainhiemvu("thuchienlainhiemvu","Thực hiện lại nhiệm vụ"),
	danhgianhiemvu("danhgianhiemvu","Đánh giá nhiệm vụ"),
	capnhattiendonhiemvu("capnhattiendonhiemvu","Cập nhật tiến độ nhiệm vụ"),
	nhacnhothuchiennhiemvu("nhacnhothuchiennhiemvu","Nhắc nhở thực hiện nhiệm vụ"),
	traodoiykiennhiemvu("traodoiykiennhiemvu","Trao đổi ý kiến nhiệm vụ"),
	traloitraodoiykiennhiemvu("traloitraodoiykiennhiemvu","Trả lời trao đổi ý kiến nhiệm vụ"),
	donvixulyphancanboxuly("donvixulyphancanboxuly","Đơn vị xử lý phân cán bộ xử lý"),
	donvixulyhuyphancanboxuly("donvixulyhuyphancanboxuly","Đơn vị xử lý hủy phân cán bộ xử lý"),
	donvihotrophancanbohotro("donvihotrophancanbohotro","Đơn vị hỗ trợ phân cán bộ hỗ trợ"),
	donvihotrohuyphancanbohotro("donvihotrohuyphancanbohotro","Đơn vị hỗ trợ hủy phân cán bộ hỗ trợ");
	
	private String key;
	private String name;
	
	TaskEventAction(String key, String name){
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

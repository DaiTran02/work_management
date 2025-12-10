package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskStatus {
	chuathuchien("chuathuchien","Chưa thực hiện", "Chưa thực hiện"),
	chuathuchien_sapquahan("chuathuchien_sapquahan","Chưa thực hiện sắp quá hạn","Sắp quá hạn"),
	chuathuchien_quahan("chuathuchien_quahan","Chưa thực hiện quá hạn","Quá hạn"),
	tuchoithuchien("tuchoithuchien", "Từ chối thực hiện",  "Từ chối"),
	thuchienlai("thuchienlai", "Thực hiện lại", "Thực hiện lại"),
	tamhoan("tamhoan","Tạm hoãn","Tạm hoãn"),
	
	dangthuchien("dangthuchien","Đang thực hiện","Đang thực hiện"),
	dangthuchien_tronghan("dangthuchien_tronghan","Đang thực hiện trong hạn","Trong hạn"),
	dangthuchien_sapquahan("dangthuchien_sapquahan","Đang thực hiện sắp quá hạn","Sắp quá hạn"),
	dangthuchien_quahan("dangthuchien_quahan","Đang thực hiện quá hạn","Quá hạn"),
	dangthuchien_khonghan("dangthuchien_khonghan","Đang thực hiện không hạn","Không hạn"),
	
	choxacnhan("choxacnhan","Chờ xác nhận","Chờ xác nhận"),
	choxacnhan_tronghan("choxacnhan_tronghan","Chờ xác nhận trong hạn","Trong hạn"),
	choxacnhan_quahan("choxacnhan_quahan","Chờ xác nhận quá hạn","Quá hạn"),
	choxacnhan_khonghan("choxacnhan_khonghan","Chờ xác nhận không hạn","Không hạn"),
	
	tuchoixacnhan("tuchoixacnhan","Từ chối xác nhận","Từ chối xác nhận"),
	
	dahoanthanh("dahoanthanh","Đã hoàn thành","Đã hoàn thành"),
	dahoanthanh_tronghan("dahoanthanh_tronghan","Đã hoàn thành trong hạn","Trong hạn"),
	dahoanthanh_quahan("dahoanthanh_quahan","Đã hoàn thành quá hạn","Quá hạn"),
	dahoanthanh_khonghan("dahoanthanh_khonghan","Đã hoàn thành không hạn","Không hạn"),
	
	has_rating("has_rating","Đã đánh giá","Đã đánh giá"),
	wait_rating("wait_rating","Chờ đánh giá","Chờ đánh giá"), 
	not_rating("not_rating","Chưa đánh giá","Chưa đánh giá"), 
	
	has_required_confirm("has_required_confirm","Có yêu cầu xác nhận hoàn thành","Có yêu cầu xác nhận"), 
	not_required_confirm("not_required_confirm","Không yêu cầu xác nhận hoàn thành","Không yêu cầu xác nhận"),
	
	has_remind("has_remind","Có nhắc nhở/đôn đốc","Có nhắc nhở/đôn đốc"), 
	not_remind("not_remind","Không có nhắc nhở/đôn đốc","Không có nhắc nhở/đôn đốc"),
	
	has_comment("has_comment","Có ý kiến/trao đổi","Có ý kiến/trao đổi"), 
	not_comment("not_comment","Không có ý kiến/trao đổi","Không có ý kiến/trao đổi"),
	
	has_process("has_process","Đã cập nhật tiến độ","Đã cập nhật tiến độ"), 
	not_process("not_process","Chưa cập nhật tiến độ","Chưa cập nhật tiến độ"),
	
	has_endtime("has_endtime","Nhiệm vụ có hạn xử lý","Có hạn xử lý"), 
	not_endtime("not_endtime","Nhiệm vụ không hạn xử lý","Không hạn xử lý"),
	
	has_subtask("has_subtask","Có giao nhiệm vụ con","Có giao nhiệm vụ con"),
	not_subtask("not_subtask","Không giao nhiệm vụ con","Không giao nhiệm vụ con");
	
	private String key;
	private String name;
	private String shortName;
	
	TaskStatus(String key, String name, String shortName){
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

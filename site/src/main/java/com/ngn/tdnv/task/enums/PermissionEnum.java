package com.ngn.tdnv.task.enums;

public enum PermissionEnum {
	
	XEMVANBANDONVI("xemvanbancuadonvi","Xem văn bản đơn vị"),
	THEMVANBAN("themvanban","Thêm văn bản"),
//	XEMVANBAN("xemvanban","Xem văn bản"),
//	XEMNHIEMVUDONVI("xemnhiemvucuadonvi","Xem nhiệm vụ đơn vị"),
//	KETTHUCNHIEMVUDONVI("ketthucnhiemvudonvi","Kết thúc nhiệm vụ đơn vị"),
//	PHANNHIEMVUDONVI("phannhiemvudonvi","Phân nhiệm vụ đơn vị cho lãnh đạo"),
//	KHONGNHIENVIEC("khongnhanviec","Không nhận việc"),
//	GIAONHIEMVU("giaonhiemvu","Giao nhiệm vụ"),
//	GIAOTHAYNHIEMVU("giaothaynhiemvu","Giao thay nhiệm vụ"),
//	BAOCAOTONGHOP("baocaotonghop","Báo cáo tổng hợp"),
	//new permission
//	CHIDAOGIAONHIEMVU("chidaovagiaonhiemvu","Chỉ đạo giao nhiệm vụ"),
	QUANLYTOGIAOVIEC("quanlytogiaoviec","Quản lý tổ giao việc")
	;
	
	private String key;
	private String title;
	
	private PermissionEnum(String key,String title) {
		this.key = key;
		this.title = title;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

}

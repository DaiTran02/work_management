package com.ngn.tdnv.task.enums;

import org.apache.commons.lang3.tuple.Pair;

public enum StatusTaskEnum {

	CHUATHUCHIEN("chuathuchien","Chưa thực hiện"),
	CHUATHUCHIEN_SAPQUAHAN("chuathuchien_sapquahan","Chưa thực hiện sắp quá hạn"),
	CHUATHUCHIEN_QUAHAN("chuathuchien_quahan","Chưa thực hiện quá hạn"),

	DANGTHUCHIEN("dangthuchien","Đang thực hiện"),
	DANGTHUCHIEN_TRONGHAN("dangthuchien_tronghan", "Đang thực hiện trong hạn"),
	DANGTHUCHIEN_SAPQUAHAN("dangthuchien_sapquahan", "Đang thực hiện sắp quá hạn"),
	DANGTHUCHIEN_QUAHAN("dangthuchien_quahan", "Đang thực hiện quá hạn"),
	DANGTHUCHIEN_KHONGHAN("dangthuchien_khonghan", "Đang thực hiện không hạn"),

	DAHOANTHANH("dahoanthanh","Đã thực hiện"),
	TAMHOAN("tamhoan","Tạm hoãn"),

	CHOXACNHAN("choxacnhan","Chờ xác nhận"),
	CHOXACNHAN_TRONGHAN("choxacnhan_tronghan", "Chờ xác nhận trong hạn"),
	CHOXACNHAN_QUAHAN("choxacnhan_quahan", "Chờ xác nhận quá hạn"),
	CHOXACNHAN_KHONGHAN("choxacnhan_khonghan", "Chờ xác nhận không hạn"),

	DAHOANTHANHQUAHAN("dahoanthanh_quahan","Đã hoàn thành quá hạn"),
	DAHOANTHANH_TRONGHAN("dahoanthanh_tronghan", "Đã hoàn thành trong hạn"),
	DAHOANTHANH_QUAHAN("dahoanthanh_quahan", "Đã hoàn thành quá hạn"),
	DAHOANTHANH_KHONGHAN("dahoanthanh_khonghan", "Đã hoàn thành không hạn"),

	TUCHOITHUCHIEN("tuchoithuchien","Từ chối nhiệm vụ"),
	TUCHOIXACNHAN("tuchoixacnhan","Từ chối xác nhận"),


	THUCHIENLAI("thuchienlai","Thực hiện lại");


	private String key;
	private String title;

	private StatusTaskEnum(String key,String title) {
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

	public static Pair<String, String> toFindKey(String key,Runnable run) {
		for(StatusTaskEnum status : StatusTaskEnum.values()) {
			if(status.key.equals(key)) {
				run.run();
				return Pair.of(status.key,status.title);
			}
		}

		return null;
	}
	
	public static String toGetKey(String key) {
		for(StatusTaskEnum status : StatusTaskEnum.values()) {
			if(status.key.equals(key)) {
				return status.getKey();
			}
		}
		return null;
	}
	
	public static String toGetTitle(String key) {
		for(StatusTaskEnum status : StatusTaskEnum.values()) {
			if(status.key.equals(key)) {
				return status.getTitle();
			}
		}
		return null;
	}
	
	public static boolean toCheckTaskIsWaitForConfirm(String key) {
		if(key.equals(CHOXACNHAN.getKey())) {
			return true;
		}
		return false;
	}
	
	public static boolean toCheckTaskIsRefuseConfirm(String key) {
		if(key.equals(TUCHOIXACNHAN.getKey())) {
			return true;
		}
		return false;
	}
	
	public static boolean toCheckTaskIsRedo(String key) {
		if(key.equals(THUCHIENLAI.getKey())) {
			return true;
		}
		return false;
	}
	
	public static boolean toCheckTaskIsCompleted(String key) {
		if(key.equals(DAHOANTHANH.getKey())) {
			return true;
		}
		return false;
	}
	
	public static boolean toCheckTaskIsNotComplete(String key) {
		if(key.equals(CHUATHUCHIEN.getKey()) || key.equals(DANGTHUCHIEN.getKey()) || key.equals(THUCHIENLAI.getKey())) {
			return true;
		}
		return false;
	}
	
	public static boolean toCheckTaskIsPedding(String key) {
		if(key.equals(TAMHOAN.getKey())) {
			return true;
		}
		return false;
	}
	
	
	//Check chua thuc hien
	public static boolean toCheckChuaThucHien(String key) {
		if(key.equals(CHUATHUCHIEN.getKey())) {
			return true;
		}
		return false;
	}
	
	//Check dang thuc hien
	public static boolean toCheckDangThucHien(String key) {
		if(key.equals(DANGTHUCHIEN.getKey()) || key.equals(DANGTHUCHIEN_KHONGHAN.getKey()) || 
				key.equals(DANGTHUCHIEN_SAPQUAHAN.getKey()) || key.equals(DANGTHUCHIEN_QUAHAN.getKey()) || key.equals(DANGTHUCHIEN_TRONGHAN.getKey())) {
			return true;
		}
		return false;
	}
	
	//Check cho xac nhan
	public static boolean toCheckChoXacNhan(String key) {
		if(key.equals(CHOXACNHAN.getKey()) || key.equals(CHOXACNHAN_TRONGHAN.getKey()) || key.equals(CHOXACNHAN_QUAHAN.getKey()) || key.equals(CHOXACNHAN_KHONGHAN.getKey())) {
			return true;
		}
		return false;
	}
	
	//Check da hoan thanh
	public static boolean toCheckDaHoanThanh(String key) {
		if(key.equals(DAHOANTHANH.getKey()) || key.equals(DAHOANTHANH_TRONGHAN.getKey()) || key.equals(DAHOANTHANH_QUAHAN.getKey()) || key.equals(DAHOANTHANH_KHONGHAN.getKey())) {
			return true;
		}
		return false;
	}
	
	//Check qua han
	public static String toCheckQuaHanVaSapQuaHan(String key, long days) {
		if(key.equals(DANGTHUCHIEN_QUAHAN.getKey()) || key.equals(CHOXACNHAN_QUAHAN.getKey()) || key.equals(CHUATHUCHIEN_QUAHAN.getKey())) {
			return "red";
		}
		
		if(key.equals(DANGTHUCHIEN_KHONGHAN.getKey()) || key.equals(CHOXACNHAN_KHONGHAN.getKey()) || key.equals(DAHOANTHANH_KHONGHAN.getKey())) {
			return "black";
		}
		
		if(days < -20279) {
			return "black";
		}
		
		if(days < 0) {
			return "red";
		}
		
		if( days > 0 && days <= 3) {
			return "rgb(239 121 0)";
		}
		
		
		return null;
	}

}

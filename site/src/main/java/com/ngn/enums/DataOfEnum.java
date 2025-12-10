package com.ngn.enums;

public enum DataOfEnum {
	TOCANHAN("User","Cá nhân"),
	DONVI("Organization","Đơn vị"),
	;
	
	private String key;
	private String title;
	
	private DataOfEnum(String key,String title) {
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

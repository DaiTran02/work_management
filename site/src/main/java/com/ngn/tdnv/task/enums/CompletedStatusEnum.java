package com.ngn.tdnv.task.enums;

public enum CompletedStatusEnum {
	QUAHAN("quahan","Quá hạn"),
	TRONGHAN("tronghan","Trong hạn"),
	KHONGHAN("khonghan","Không hạn");
	
	
	private String key;
	private String title;
	
	private CompletedStatusEnum(String key,String title) {
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
	
	public static String findComletedStatus(String key) {
		for(CompletedStatusEnum value: CompletedStatusEnum.values()) {
			if(value.getKey().equals(key)) {
				return value.getTitle();
			}
		}
		return "";
	}
	
}

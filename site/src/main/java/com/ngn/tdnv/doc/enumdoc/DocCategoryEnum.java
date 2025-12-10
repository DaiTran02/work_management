package com.ngn.tdnv.doc.enumdoc;

public enum DocCategoryEnum {
	CVDI("CVDi","Văn bản đi"),
	CVDEN("DVden","Văn bản đến");
	
	private String key;
	private String title;
	
	private DocCategoryEnum(String key,String title) {
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

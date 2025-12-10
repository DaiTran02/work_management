package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskCompleteStatus {
	quahan("quahan", "Quá hạn"),
	tronghan("tronghan", "Trong hạn"),
	khonghan("khonghan", "Không hạn");
	
	private String key;
	private String name;
	
	TaskCompleteStatus(String key, String name){
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

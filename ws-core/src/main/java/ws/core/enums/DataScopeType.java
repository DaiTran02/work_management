package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DataScopeType {
	incChildOrgs("incChildOrgs","Đơn vị hiện và các đơn vị con 1 cấp"), 
	incChildOrgsAndExcMyOrg("incChildOrgsAndExcMyOrg", "Đơn vị con 1 cấp và trừ Đơn vị trên");
	
	private String key;
	private String name;
	
	DataScopeType(String key, String name){
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

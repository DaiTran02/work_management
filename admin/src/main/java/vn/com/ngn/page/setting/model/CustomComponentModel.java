package vn.com.ngn.page.setting.model;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Data;

@Data
public class CustomComponentModel {
	private String id;
	private String orgId; //Đơn vị sử dụng
	private String nav; //Nơi sử dụng
	private String function; //Chức năng: filter hay là trường thêm dữ liệu
	private List<CustomComponent> components;
	
	@Data
	public class CustomComponent{
		private String type;
		private Property properties;
	}
	
	@Data
	public class Property{
		private String name;
		private String key;//Danh cho txt
		private List<Pair<String, String>> item;//Danh cho select
		private String placeholder;
		private String textColor;
		private String background;
		private String label;
		private String helperText;
		
		
	}
}
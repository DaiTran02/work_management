package ws.core.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ConfigurationKey {
	hourWorkStartOfDayEvent("hourWorkStartOfDayEvent", "Giờ đầu ngày của Event", "Cấu hình giờ cho Event", ConfigurationType.Integer.name(), ConfigurationObject.event.name(), "7"),
	minuteWorkStartOfDayEvent("minuteWorkStartOfDayEvent", "Phút đầu ngày của Event", "Cấu hình phút cho Event", ConfigurationType.Integer.name(), ConfigurationObject.event.name(), "0"),
	hourLunchOfDayEvent("hourLunchOfDayEvent", "Giờ nghỉ trưa của Event", "Cấu hình giờ cho Event", ConfigurationType.Integer.name(), ConfigurationObject.event.name(), "12"),
	minuteLunchOfDayEvent("minuteLunchOfDayEvent", "Phút nghỉ trưa của Event", "Cấu hình phút cho Event", ConfigurationType.Integer.name(), ConfigurationObject.event.name(), "0"),
	hourWorkEndOfDayEvent("hourWorkEndOfDayEvent", "Giờ cuối ngày của Event", "Cấu hình giờ cho Event", ConfigurationType.Integer.name(), ConfigurationObject.event.name(), "17"),
	minuteWorkEndOfDayEvent("minuteWorkEndOfDayEvent", "Phút cuối ngày của Event", "Cấu hình phút cho Event", ConfigurationType.Integer.name(), ConfigurationObject.event.name(), "0");
	
	private String key;
	private String name;
	private String description;
	private String type;
	private String object;
	private String defaultValue;
	
	ConfigurationKey(String key, String name, String description, String type, String object, String defaultValue){
		this.key=key;
		this.name=name;
		this.description=description;
		this.type=type;
		this.object=object;
		this.defaultValue=defaultValue;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}

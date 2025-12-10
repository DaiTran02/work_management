package ws.core.model.embeded;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskEventItemDescription {
	@Field(value = "name")
	private String name;
	
	@Field(value = "description")
	private String description;
	
	public TaskEventItemDescription(String name, String description) {
		this.name=name;
		this.description=description;
	}
}

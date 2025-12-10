package ws.core.model.embeded;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class TaskDocInfo {
	@Indexed
	@Field(value = "number")
	private String number;
	
	@Indexed
	@Field(value = "symbol")
	private String symbol;
	
	@Indexed
	@Field(value = "summary")
	private String summary;
	
	@Indexed
	@Field(value = "category")
	public String category;
	
	public TaskDocInfo() {
		
	}
}

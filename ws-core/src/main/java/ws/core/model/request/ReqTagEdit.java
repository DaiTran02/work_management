package ws.core.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqTagEdit {
	@NotBlank(message = "name không được trống")
	private String name;
	
	@NotBlank(message = "color không được trống")
	private String color;
	
	@NotNull(message = "active không được trống")
	private Boolean active;
	
	public ReqTagEdit() {
		
	}
}

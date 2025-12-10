package ws.core.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqEventResourceUpdate {
	@NotNull(message = "name không được trống")
	private String name;
	
	private String description;
}

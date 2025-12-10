package ws.core.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqClassifyTaskUpdate {
	@NotNull(message = "name không được trống")
	@ValidStringMedium(message = "name không được chứa các ký tự đặc biệt")
	public String name;
	
	public int order=1;
	
	public boolean active = true;
}

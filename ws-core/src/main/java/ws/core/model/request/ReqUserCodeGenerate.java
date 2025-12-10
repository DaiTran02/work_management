package ws.core.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqUserCodeGenerate {
	@NotBlank(message = "username không được trống")
	private String username;
}
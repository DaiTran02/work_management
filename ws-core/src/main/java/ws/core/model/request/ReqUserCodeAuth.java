package ws.core.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReqUserCodeAuth {
	@NotEmpty(message = "code không được trống")
	@Schema(name = "code", description = "Code của người dùng", required = true, example = "abc123")
	private String code;
}
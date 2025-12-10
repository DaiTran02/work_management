package ws.core.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReqUserLogin {
	@NotEmpty(message = "username không được trống")
	@Schema(name = "username", description = "Tài khoản người dùng", required = true, example = "khuetech")
	private String username;
	
	@NotEmpty(message = "password không được trống")
	@Schema(name = "password", description = "Mật khẩu người dùng", required = true, example = "abc123")
	private String password;
}
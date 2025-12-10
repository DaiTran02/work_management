package ws.core.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidPassword;

@Data
public class ReqUserResetPassword {
	@NotNull(message = "passwordNew không được trống")
	@ValidPassword(message = "passwordNew không hợp lệ")
	private String passwordNew;
}

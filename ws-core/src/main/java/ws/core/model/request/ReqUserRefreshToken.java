package ws.core.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReqUserRefreshToken {
	@NotEmpty(message = "refreshToken không được trống")
	@Schema(name = "refreshToken", description = "Mã token dùng để cấp token mới", required = true, example = "5Nzg2MzY3LCJleHAiOjE3MTA2NTAzNjd")
	public String refreshToken;
}
package ws.core.model.request.site;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidPassword;

@Data
public class ReqUserChangePasswordSite {
	@NotNull(message = "passwordOld không được trống")
	public String passwordOld;
	
	@NotNull(message = "passwordNew không được trống")
	@ValidPassword(message = "passwordNew phải đúng chính sách")
	public String passwordNew;
}

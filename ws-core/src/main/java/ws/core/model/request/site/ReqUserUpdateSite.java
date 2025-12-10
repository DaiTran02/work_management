package ws.core.model.request.site;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag;
import lombok.Data;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqUserUpdateSite {
	@NotNull(message = "email không được trống")
	@Email(message = "email không đúng định dạng")
	private String email;
	
	@Pattern(message = "phone chỉ bao gồm số", regexp = "^[0-9]{0,15}$", flags = Flag.UNICODE_CASE)
	private String phone;
	
	@NotNull(message = "fullName không được trống")
	@ValidStringMedium(message = "fullName không được chứa các ký tự đặc biệt")
	private String fullName;
}

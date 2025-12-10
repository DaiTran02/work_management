package ws.core.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag;
import lombok.Data;
import ws.core.validation.ValidCode;
import ws.core.validation.ValidPassword;
import ws.core.validation.ValidStringMedium;
import ws.core.validation.ValidUsername;

@Data
public class ReqUserCreate {
	@NotNull(message = "username không được trống")
	@ValidUsername(message = "username không được chứa các ký tự đặc biệt và khoảng trắng")
	private String username;
	
	@NotNull(message = "password không được trống")
	@ValidPassword(message = "password phải đúng chính sách")
	private String password;
	
	@NotNull(message = "email không được trống")
	@Email(message = "email không đúng định dạng")
	private String email;
	
	@Pattern(message = "phone chỉ bao gồm số", regexp = "^[0-9]{0,15}$", flags = Flag.UNICODE_CASE)
	private String phone;
	
	@NotNull(message = "fullName không được trống")
	@ValidStringMedium(message = "fullName không được chứa các ký tự đặc biệt")
	private String fullName;
	
	@ValidStringMedium(message = "jobTitle không được chứa các ký tự đặc biệt")
	private String jobTitle;
	
	@NotNull(message = "active không được trống")
	private boolean active;
	
	@ValidCode(message = "activeCode chỉ bao gồm số và chữ")
	private String activeCode;
}

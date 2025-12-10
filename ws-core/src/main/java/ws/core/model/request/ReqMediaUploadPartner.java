package ws.core.model.request;


import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidFile;

@Data
public class ReqMediaUploadPartner {
	@NotNull(message = "file không được rỗng")
    @ValidFile(message = "Chỉ chấp nhận những tệp: pdf, doc, docx, xls, xlsx, png, jpg, jpeg, zip, rar")
	private MultipartFile file=null;
	
	@NotBlank(message = "organizationCode không được trống")
	private String organizationCode;
	
	@NotBlank(message = "username không được trống")
	private String username;
	
	private String description=null;
}

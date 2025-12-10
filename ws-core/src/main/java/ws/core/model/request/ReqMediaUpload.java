package ws.core.model.request;


import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.embeded.Creator;
import ws.core.validation.ValidFile;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqMediaUpload {
	
	@NotNull(message = "file không được rỗng")
	@ValidFile(message = "chỉ chấp nhận những tệp: pdf,doc,docx,xls,xlsx,png,jpg,jpeg,zip,rar")
	public MultipartFile file=null;
	
	@NotNull(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	public String organizationId;
	
	@NotNull(message = "organizationName không được trống")
	@ValidStringMedium(message = "organizationName không được chứa các ký tự đặc biệt")
	public String organizationName;
	
	@NotNull(message = "organizationUserId không được trống")
	@ValidObjectId(message = "organizationUserId không hợp lệ")
	public String organizationUserId;
	
	@NotNull(message = "organizationUserName không được trống")
	@ValidStringMedium(message = "organizationUserName không được chứa các ký tự đặc biệt")
	public String organizationUserName;
	
	public String description=null;
	
	public Creator toCreator() {
		Creator creator=new Creator();
		creator.setOrganizationId(getOrganizationId());
		creator.setOrganizationName(getOrganizationName());
		creator.setOrganizationUserId(getOrganizationUserId());
		creator.setOrganizationUserName(getOrganizationUserName());
		return creator;
	}
}

package ws.core.model.request;

import java.util.LinkedList;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqRoleOrganizationUpdate {
	@NotNull(message = "name không được trống")
	@ValidStringMedium(message = "name không được chứa các ký tự đặc biệt")
	private String name;
	
	@NotNull(message = "description không được trống")
	@ValidStringMedium(message = "description không được chứa các ký tự đặc biệt")
	private String description;
	
	private LinkedList<String> permissionKeys = new LinkedList<String>();
	private LinkedList<String> userIds = new LinkedList<String>();
	
	@ValidObjectId(message = "roleTemplateId không hợp lệ")
	private String roleTemplateId;
}

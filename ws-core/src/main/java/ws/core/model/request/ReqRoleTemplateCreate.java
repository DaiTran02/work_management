package ws.core.model.request;

import java.util.LinkedList;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqRoleTemplateCreate {
	@NotNull(message = "name không được trống")
	@ValidStringMedium(message = "name không được chứa các ký tự đặc biệt")
	public String name;
	
	@ValidStringMedium(message = "description không được chứa các ký tự đặc biệt")
	public String description;
	
	public LinkedList<String> permissionKeys=new LinkedList<String>();
	
	public boolean active;
	
	public ReqRoleTemplateCreate() {
		active=true;
	}
}

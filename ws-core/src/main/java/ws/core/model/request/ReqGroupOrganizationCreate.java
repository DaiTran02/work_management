package ws.core.model.request;

import java.util.LinkedList;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqGroupOrganizationCreate {
	@NotNull(message = "name không được trống")
	@ValidStringMedium(message = "name không được chứa các ký tự đặc biệt")
	public String name;
	
	@NotNull(message = "description không được trống")
	@ValidStringMedium(message = "description không được chứa các ký tự đặc biệt")
	public String description;
	
	public LinkedList<String> userIds = new LinkedList<String>();
	
	public ReqGroupOrganizationCreate() {
		
	}
}

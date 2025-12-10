package ws.core.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqOrganizationUpdatePartner {
	@NotNull(message = "name không được trống")
	@ValidStringMedium(message = "name không được chứa các ký tự đặc biệt")
	private String name;
	
	@NotNull(message = "description không được trống")
	@ValidStringMedium(message = "description không được chứa các ký tự đặc biệt")
	private String description;
	
	@ValidStringMedium(message = "parentUnitCode không được chứa các ký tự đặc biệt")
	private String parentUnitCode;
	
	private boolean active;
	
	private int order;
}

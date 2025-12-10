package ws.core.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.enums.OrganizationLevel;
import ws.core.validation.ValidEnum;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqOrganizationCreate {
	@NotNull(message = "name không được trống")
	@ValidStringMedium(message = "name không được chứa các ký tự đặc biệt")
	private String name;
	
	@NotNull(message = "description không được trống")
	@ValidStringMedium(message = "description không được chứa các ký tự đặc biệt")
	private String description;
	
	@ValidObjectId(message = "parentId không hợp lệ")
	private String parentId;
	
	@ValidStringMedium(message = "unitCode không được chứa các ký tự đặc biệt")
	private String unitCode;
	
	@ValidStringMedium(message = "organizationCategoryId không được chứa các ký tự đặc biệt")
	private String organizationCategoryId;
	
	private boolean active;
	private int order;
	
	@NotBlank(message = "level không được trống")
	@ValidEnum(message = "level không hợp lệ", enumClass = OrganizationLevel.class)
	private String level;
	
	public ReqOrganizationCreate() {
		active=true;
		order=1;
	}
}

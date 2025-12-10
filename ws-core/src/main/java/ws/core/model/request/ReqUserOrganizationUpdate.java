package ws.core.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqUserOrganizationUpdate {
	@NotNull(message = "positionName không được trống")
	@ValidStringMedium(message = "positionName không được chứa các ký tự đặc biệt")
	public String positionName;
	
	@NotNull(message = "accountIOffice không được trống")
	@ValidStringMedium(message = "accountIOffice không được chứa các ký tự đặc biệt")
	public String accountIOffice;
	
	public boolean active = true;
}

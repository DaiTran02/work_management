package ws.core.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqImportOrganizationsFile {
	@NotNull(message = "vị trí [nameColumPosition] không được trống")
	private String nameColumPosition;
	
	@NotNull(message = "vị trí [descriptionColumPosition] không được trống")
	private String descriptionColumPosition;
	
	@NotNull(message = "vị trí [unitCodeColumPosition] không được trống")
	private String unitCodeColumPosition;
	
	
	
	public ReqImportOrganizationsFile() {
		
	}
}

package ws.core.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;

/**
 * The Class ReqTaskDoUnAssignUserSupport.
 */
@Data
public class ReqTaskDoUnAssignUserSupport {
	
	/** The reason. */
	@NotNull(message = "reason không được trống")
//	@ValidStringMedium(message = "reason không được chứa các ký tự đặc biệt")
	public String reason;
	
	/** The creator. */
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	/**
	 * Instantiates a new req task do un assign user support.
	 */
	public ReqTaskDoUnAssignUserSupport() {
		
	}
}

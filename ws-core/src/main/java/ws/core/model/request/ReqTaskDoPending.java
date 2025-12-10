package ws.core.model.request;

import java.util.LinkedList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqTaskDoPending {
	@NotNull(message = "reasonPending không được trống")
	@ValidStringMedium(message = "reasonPending không được chứa các ký tự đặc biệt")
	public String reasonPending;
	
	public LinkedList<String> attachments=new LinkedList<String>();
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public ReqTaskDoPending() {
		
	}
}

package ws.core.model.request;

import java.util.LinkedList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;

@Data
public class ReqTaskDoRemind {
	
	@NotNull(message = "reasonRemind không được trống")
//	@ValidStringMedium(message = "reasonRemind không được chứa các ký tự đặc biệt")
	public String reasonRemind;
	
	public LinkedList<String> attachments=new LinkedList<String>();
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public ReqTaskDoRemind() {
		
	}
}

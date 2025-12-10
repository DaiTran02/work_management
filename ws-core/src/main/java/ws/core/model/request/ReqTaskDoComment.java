package ws.core.model.request;

import java.util.LinkedList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;

@Data
public class ReqTaskDoComment {
	@NotNull(message = "message không được trống")
//	@ValidStringMedium(message = "message không được chứa các ký tự đặc biệt")
	public String message;
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public LinkedList<String> attachments=new LinkedList<String>();
	
	public ReqTaskDoComment() {
		
	}
}

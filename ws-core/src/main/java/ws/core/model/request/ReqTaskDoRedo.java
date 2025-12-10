package ws.core.model.request;

import java.util.LinkedList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqTaskDoRedo {
	@NotNull(message = "reasonRedo không được trống")
	@ValidStringMedium(message = "reasonRedo không được chứa các ký tự đặc biệt")
	public String reasonRedo;
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public LinkedList<String> attachments=new LinkedList<String>();
	
	public ReqTaskDoRedo() {
		
	}
}

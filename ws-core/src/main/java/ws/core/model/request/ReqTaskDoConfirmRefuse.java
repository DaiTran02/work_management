package ws.core.model.request;

import java.util.LinkedList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;

@Data
public class ReqTaskDoConfirmRefuse {
	@NotNull(message = "reasonConfirmRefuse không được trống")
//	@ValidStringMedium(message = "reasonConfirmRefuse không được chứa các ký tự đặc biệt")
	public String reasonConfirmRefuse;
	
	public LinkedList<String> attachments=new LinkedList<String>();
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public ReqTaskDoConfirmRefuse() {
		
	}
}

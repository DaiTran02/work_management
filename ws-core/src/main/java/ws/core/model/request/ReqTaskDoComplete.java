package ws.core.model.request;

import java.util.LinkedList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;

@Data
public class ReqTaskDoComplete {
	public long completedTime=0;
	
	public LinkedList<String> attachments=new LinkedList<String>();
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	private Boolean ignoreRequiredConfirm=false;
	
	public ReqTaskDoComplete() {
		
	}
}

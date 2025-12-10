package ws.core.model.request;

import java.util.LinkedList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;

@Data
public class ReqTaskDoUpdateProcess {
	@Min(value = 0, message = "percent tối thiểu 0")
	@Max(value = 100, message = "percent tối đa 100")
	public int percent;
	
	@NotNull(message = "explain không được trống")
//	@ValidStringMedium(message = "explain không được chứa các ký tự đặc biệt")
	public String explain;
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public LinkedList<String> attachments=new LinkedList<String>();
	
	public ReqTaskDoUpdateProcess() {
		
	}
}

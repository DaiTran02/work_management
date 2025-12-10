package ws.core.model.request;

import java.io.Serializable;
import java.util.LinkedList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.validation.ValidListObjectId;
import ws.core.validation.ValidStringExtra;

@SuppressWarnings("serial")
@Data
public class ReqDocConfirmComplete implements Serializable {
	public long completedTime=0;
	
	@NotNull(message = "content không được trống")
	@ValidStringExtra(message = "content không được chứa các ký tự đặc biệt")
	private String content;
	
	@ValidListObjectId(message = "attachments có item không hợp lệ")
	private LinkedList<String> attachments=new LinkedList<String>();
	
	@Valid
	@NotNull(message = "creator không được trống")
	private ReqCreator creator;
	
	public ReqDocConfirmComplete() {
		
	}
}

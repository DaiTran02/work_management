package ws.core.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqTaskDoRating {
	@Min(value = 1, message = "star tối thiểu là 1 sao")
	@Max(value = 5, message = "star tối đa là 5 sao")
	public int star;
	
	@NotNull(message = "explain không được trống")
	@ValidStringMedium(message = "explain không được chứa các ký tự đặc biệt")
	public String explain;
	
	public Double markA;
	public Double markB;
	public Double markC;
	
	@Valid
	@NotNull(message = "creator không được trống")
	public ReqCreator creator;
	
	public ReqTaskDoRating() {
		
	}
}

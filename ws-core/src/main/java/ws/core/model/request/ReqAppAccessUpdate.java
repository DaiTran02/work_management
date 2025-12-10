package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ws.core.validation.ValidDatetimeLong;
import ws.core.validation.ValidObjectId;
import ws.core.validation.ValidStringMedium;

@Data
public class ReqAppAccessUpdate {
	@NotNull(message = "name không được trống")
	@ValidStringMedium(message = "name không được chứa các ký tự đặc biệt")
	public String name;
	
	@NotNull(message = "description không được trống")
	@ValidStringMedium(message = "description không được chứa các ký tự đặc biệt")
	public String description;
	
	@NotNull(message = "organizationId không được trống")
	@ValidObjectId(message = "organizationId không hợp lệ")
	public String organizationId;
	
	@NotNull(message = "startTime không được trống")
	@ValidDatetimeLong(message = "startTime không hợp lệ")
	public long startTime;
	
	@NotNull(message = "endTime không được trống")
	@ValidDatetimeLong(message = "endTime không hợp lệ")
	public long endTime;
	
	public boolean active;
	
	public List<String> ipsAccess=new ArrayList<String>();
	
	public ReqAppAccessUpdate() {
		active=true;
	}
}

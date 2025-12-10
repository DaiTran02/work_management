package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReqUserOrganizationAdds {
	@NotEmpty(message = "userIds không được trống")
	public List<String> userIds=new ArrayList<String>();
}

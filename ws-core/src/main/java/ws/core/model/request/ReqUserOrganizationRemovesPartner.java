package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReqUserOrganizationRemovesPartner {
	@NotEmpty(message = "userNames không được trống")
	public List<String> userNames=new ArrayList<String>();
}

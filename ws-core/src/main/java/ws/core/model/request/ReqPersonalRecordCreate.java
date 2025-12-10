package ws.core.model.request;

import java.util.List;

import lombok.Data;
import ws.core.model.request.embeded.ReqCreator;
import ws.core.validation.ValidObjectId;

@Data
public class ReqPersonalRecordCreate {
	@ValidObjectId(message = "userId không hợp lệ")
	public String userId;
	public String title;
	public String description;
	public List<String> docs;
	public List<String> tasks;
	public ReqCreator creator;
}

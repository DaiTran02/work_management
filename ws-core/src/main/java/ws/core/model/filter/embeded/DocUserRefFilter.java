package ws.core.model.filter.embeded;

import lombok.Data;

@Data
public class DocUserRefFilter{
	private String organizationId=null;
	private String organizationUserId=null;
}

package ws.core.model.request;

import lombok.Data;

@Data
public class ReqLoginByCodeCreate {
	private String username;
	private String fullName;
}

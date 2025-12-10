package ws.core.model.request;

import lombok.Data;

@Data
public class ReqPartnerItemUser {
	private String userName;
	private String userFullName;
	private String jobPositionName;
	private String statusName;
	private int statusId;
	private String unitName;
	private String identifer;
}

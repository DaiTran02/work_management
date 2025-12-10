package ws.core.model.filter;

import lombok.Data;

@Data
public class PersonalRecordFilter {
	private String keySearch;
	private String userId;
	private String oldUserId;
	private String transferredUserId;
}

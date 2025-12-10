package ws.core.model.request;

import java.util.List;

import lombok.Data;

@Data
public class ReqPartnerTreeUnit {
	private String identifier;
	private String name;
	private List<ReqPartnerTreeUnit> children;
}

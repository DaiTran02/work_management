package ws.core.model.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ReqUserImportFromLdap {
	private List<String> usernames=new ArrayList<>();
}

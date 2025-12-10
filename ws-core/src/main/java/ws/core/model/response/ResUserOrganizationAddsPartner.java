package ws.core.model.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import ws.core.model.Organization;

@Data
public class ResUserOrganizationAddsPartner {
	private Organization organization;
	private List<String> successes = new ArrayList<>();
	private List<String> failes = new ArrayList<>();
}

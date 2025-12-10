package ws.core.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqConfigurationUpdate {
	@NotNull(message = "value không được null")
	private String value;
}

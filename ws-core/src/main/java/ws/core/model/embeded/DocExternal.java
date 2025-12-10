package ws.core.model.embeded;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class DocExternal {
	@Indexed
	@Field(value = "packageId")
	private String packageId;
	
	@Indexed
	@Field(value = "officeId")
	private String officeId;
}

package ws.core.model.embeded;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class Actor {
	/**
	 * Id người thực hiện gọi api
	 */
	@Field(value = "actorId")
	private String actorId;
	
	/**
	 * Username người thực hiện gọi api
	 */
	@Field(value = "actorName")
	private String actorName;
	
	/**
	 * Tên người thực hiện gọi api
	 */
	@Field(value = "actorFullName")
	private String actorFullName;
}

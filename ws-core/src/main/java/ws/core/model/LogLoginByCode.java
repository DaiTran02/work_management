package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "log_login_by_code")
public class LogLoginByCode {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Field(value = "username")
	private String username;
	
	@Field(value = "fullname")
	private String fullName;
	
	@Field(value = "datelogin")
	private Date dateLogin;
}

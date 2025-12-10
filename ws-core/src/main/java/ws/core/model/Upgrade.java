package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "upgrade")
public class Upgrade {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "upgradedTime")
	private Date upgradedTime;
	
	@Indexed(unique = true)
	@Field(value = "name")
	private String name;
	
	@Field(value = "description")
	private String description;
	
	public Upgrade() {
		this.id=ObjectId.get();
		this.upgradedTime=new Date();
	}
}

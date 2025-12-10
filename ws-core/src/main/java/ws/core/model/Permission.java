package ws.core.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "permission")
public class Permission {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed(unique = true)
	@Field(value = "key")
	private String key;
	
	@Indexed
	@Field(value = "name")
	private String name;
	
	@Indexed
	@Field(value = "description")
	private String description;
	
	@Indexed
	@Field(value="order")
	private int order;
	
	@Indexed
	@Field(value = "groupId")
	private String groupId;
	
	@Indexed
	@Field(value = "groupName")
	private String groupName;
	
	@Indexed
	@Field(value="groupOrder")
	private int groupOrder;
	
	public Permission() {
		this.id=ObjectId.get();
	}
	
	public ObjectId getObjectId() {
		return id;
	}
	
	public String getId() {
		return id.toHexString();
	}
}

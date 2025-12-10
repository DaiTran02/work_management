package ws.core.model;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.model.embeded.CreatorInfo;
import ws.core.model.embeded.PersonalUser;

@Data
@Document(collection = "personal_record")
public class PersonalRecord {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updaeTime")
	private Date updatedTime;
	
	@Field(value = "title")
	private String title;
	
	@Field(value = "description")
	private String description;
	
	@Field(value = "rootUser")
	private PersonalUser rootUser;
	
	// Người hiện tại sử dụng hồ sơ
	@Field(value = "currentUser")
	private PersonalUser currentUser;
	
	// Người chuyển giao hồ sơ
	@Field(value = "oldUsers")
	private List<PersonalUser> oldUsers;
	
	// Người được chuyển giao hồ sơ
	@Field(value = "usersTransfered")
	private List<PersonalUser> usersTransfered;
	
	@Field(value = "transferTime")
	private Date transferTime;
	
	@Field(value = "docs")
	private List<String> docs;
	
	@Field(value = "tasks")
	private List<String> tasks;
	
	@Field(value = "creator")
	private CreatorInfo creator;
}

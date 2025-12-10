package ws.core.model.embeded;

import java.util.Date;
import java.util.List;

import org.bson.Document;

import lombok.Data;

@Data
public class PersonalRecordDetail {
	private String id;
	private Date createdTime;
	private Date updatedTime;
	private String title;
	private String description;
	private PersonalUser currentUser;
	private Date transferTime;
	private List<PersonalUser> oldUsers;
	private List<PersonalUser> usersTransfered;
	private List<Document> docs;
	private List<Document> tasks;
	private CreatorInfo creator;
}

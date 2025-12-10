package ws.core.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.model.embeded.Creator;

@Data
@Document(collection = "media")
public class Media {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@TextIndexed
	@Field(value = "fileName")
	private String fileName;
	
	@TextIndexed
	@Field(value = "fileDescription")
	private String fileDescription;
	
	@TextIndexed
	@Field(value = "fileType")
	private String fileType;
	
	@Field(value = "fileSize")
	private long fileSize;
	
	@Field(value = "filePath")
	private String filePath;
	
	@Indexed
	@Field(value = "creator")
	private Creator creator;
	
	public Media() {
		this.id=ObjectId.get();
		this.createdTime=new Date();
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTimeLong() {
		if(createdTime!=null)
			return createdTime.getTime();
		return 0;
	}
	
	public long getUpdatedTimeLong() {
		if(updatedTime!=null)
			return updatedTime.getTime();
		return 0;
	}
	
}

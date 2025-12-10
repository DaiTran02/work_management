package ws.core.model.embeded;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class Attachment {
	@Field(value = "_id")
	private ObjectId id;
	
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Field(value = "fileName")
	private String fileName;
	
	@Field(value = "fileDescription")
	private String fileDescription;
	
	@Field(value = "fileType")
	private String fileType;
	
	@Field(value = "fileSize")
	private long fileSize;
	
	@Field(value = "filePath")
	private String filePath;
	
	@Field(value = "creator")
	private Creator creator;
	
	@Field(value = "external")
	private External external;
	
	public Attachment() {
		this.id=ObjectId.get();
	}
	
	@Data
	public static class External{
		@Field(value = "idPackage")
		private String idPackage;
		
		@Field(value = "idOffice")
		private String idOffice;
	}
	
	public String getId() {
		return id.toHexString();
	}
	
	public long getCreatedTime() {
		if(createdTime!=null) {
			return createdTime.getTime();
		}
		return 0;
	}
	
	public long getUpdatedTime() {
		if(updatedTime!=null) {
			return updatedTime.getTime();
		}
		return 0;
	}
}

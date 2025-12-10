package ws.core.model.embeded;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class DocResultConfirm {
	@Field(value = "confirmedTime")
	private Date confirmedTime;
	
	@Indexed
	@Field(value = "completedTime")
	private Date completedTime;
	
	@Field(value = "content")
	private String content;
	
	@Field(value = "attachments")
	private List<String> attachments=new ArrayList<>();
	
	@Field(value = "creator")
	private CreatorInfo creator;
	
	public DocResultConfirm() {
		this.confirmedTime=new Date();
	}
	
	public long getConfirmedTime() {
		if(confirmedTime!=null) {
			return confirmedTime.getTime();
		}
		return 0;
	}
	
	public long getCompletedTime() {
		if(completedTime!=null) {
			return completedTime.getTime();
		}
		return 0;
	}
	
}

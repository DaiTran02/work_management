package ws.core.model.embeded;

import java.util.Date;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.util.TextUtil;

@Data
public class GroupOrganizationExpand {
	@Indexed
	@Field(value = "groupId")
	private String groupId;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Indexed
	@Field(value = "name")
	private String name;
	
	@Field(value = "description")
	private String description;
	
	@Field(value = "creatorId")
	private String creatorId;
	
	@Field(value = "creatorName")
	private String creatorName;
	
	@Indexed
	@Field(value = "userIds")
	private LinkedList<String> userIds = new LinkedList<String>();
	
	@Indexed
	@Field(value = "active")
	private boolean active;
	
	@Indexed
	@Field(value = "archive")
	private boolean archive;
	
	public GroupOrganizationExpand() {
		this.groupId=ObjectId.get().toHexString();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.active=true;
		this.archive=false;
	}
	
	public long getCreatedTimeLong() {
		if(getCreatedTime()!=null) {
			return getCreatedTime().getTime();
		}
		return 0;
	}
	
	public long getUpdatedTimeLong() {
		if(getUpdatedTime()!=null) {
			return getUpdatedTime().getTime();
		}
		return 0;
	}
	
	public boolean isCondition(String keyword) {
		if(keyword==null || keyword.equals(""))
			return true;
		
		/*Có dấu*/
		String searching=getName()+" "+getDescription();
		searching+=" "+TextUtil.processWords(searching);
		
		/*Không dấu*/
		String noncharacters=TextUtil.removeAccent(searching);
		searching+=" "+TextUtil.processWords(noncharacters) + " " + noncharacters;
		
		return searching.toLowerCase().contains(keyword.toLowerCase()) || searching.toLowerCase().contains(TextUtil.removeAccent(keyword.toLowerCase()));
	}
}

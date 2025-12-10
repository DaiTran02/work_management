package ws.core.model.embeded;

import java.util.Date;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class RoleOrganizationExpand {
	@Indexed
	@Field(value = "roleId")
	private String roleId;
	
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
	@Field(value = "permissionKeys")
	private LinkedList<String> permissionKeys = new LinkedList<String>();
	
	@Indexed
	@Field(value = "userIds")
	private LinkedList<String> userIds = new LinkedList<String>();
	
	@Indexed
	@Field(value = "roleTemplateId")
	private String roleTemplateId;
	
	@Indexed
	@Field(value = "active")
	private boolean active;
	
	@Indexed
	@Field(value = "archive")
	private boolean archive;
	
	public RoleOrganizationExpand() {
		this.roleId=ObjectId.get().toHexString();
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.active=true;
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
	
	public String getType() {
		if(getRoleTemplateId()==null) {
			return "local";
		}else {
			return "global";
		}
	}
}

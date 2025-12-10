package ws.core.model.embeded;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class UserOrganizationExpand {
	@Indexed
	@Field(value = "userId")
	private String userId;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Indexed
	@Field(value = "userName")
	private String userName;
	
	@Field(value = "fullName")
	private String fullName;
	
	@Indexed
	@Field(value = "positionName")
	private String positionName;
	
	@Indexed
	@Field(value = "accountIOffice")
	private String accountIOffice;
	
	@Indexed
	@Field(value = "active")
	private boolean active;
	
	@Indexed
	@Field(value = "archive")
	private boolean archive;
	
	@Field(value = "addFromSource")
	private AddFromSource addFromSource;
	
	public UserOrganizationExpand(){
		this.createdTime=new Date();
		this.updatedTime=new Date();
		this.active=true;
		this.archive=false;
		this.addFromSource = AddFromSource.admin;
	}
	
	public static enum AddFromSource {
		admin, partner, self;
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
}

package ws.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import ws.core.enums.UserProvider;
import ws.core.model.embeded.Actor;
import ws.core.model.embeded.FirstReview;
import ws.core.model.embeded.LdapUser;

@Data
@Document(collection = "user")
public class User {
	@Id
	@Field(value = "_id")
	private ObjectId id;
	
	@Indexed
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Indexed
	@Field(value = "updatedTime")
	private Date updatedTime;
	
	@Indexed(unique = true)
	@Field(value="username")
	private String username;
	
	@Indexed
	@Field(value="email")
	private String email;
	
	@Indexed
	@Field(value="phone")
	private String phone;
	
	@Field(value = "password")
	private String password;
	
	@Indexed
	@Field(value = "fullName")
	private String fullName;
	
	@Indexed
	@Field(value = "jobTitle")
	private String jobTitle;
	
	@Indexed
	@Field(value = "creatorId")
	public String creatorId;
	
	@Field(value="creatorName")
	public String creatorName;
	
	@Indexed
	@Field(value="active")
	private boolean active;
	
	@Indexed
	@Field(value="archive")
	private boolean archive;
	
	@Indexed
	@Field(value="activeCode")
	private String activeCode;
	
	@Field(value = "lastDateLogin")
	private Date lastDateLogin;
	
	@Field(value = "lastChangePassword")
	private Date lastChangePassword;
	
	@Field(value = "lastIPLogin")
	private String lastIPLogin;
	
	@Indexed
	@Field(value="belongOrganizations")
	private List<BelongOrganization> belongOrganizations=new ArrayList<BelongOrganization>();
	
	@Field(value = "firstReview")
	private FirstReview firstReview;
	
	@Field(value = "guideWebUI")
	private boolean guideWebUI;
	
	@Indexed
	@Field(value="provider")
	private UserProvider provider;
	
	@Indexed
	@Field(value="ldapInfo")
	private LdapUser ldapInfo;
	
	@Field(value="meta")
	private Object meta; 
	
	public User(){
		this.id=new ObjectId();
		this.createdTime=new Date();
		this.active=true;
		this.archive=false;
		this.guideWebUI=false;
		this.provider=UserProvider.local;
	}
	
	@Data
	public static class BelongOrganization{
		@Indexed
		@Field(value="organizationId")
		private String organizationId;
		
		@Field(value="organizationName")
		private String organizationName;
	}
	
	public ObjectId getObjectId() {
		return id;
	}
	
	public String getId() {
		return id.toHexString();
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
	
	public long getLastDateLoginLong() {
		if(getLastDateLogin()!=null) {
			return getLastDateLogin().getTime();
		}
		return 0;
	}
	
	public long getLastChangePasswordLong() {
		if(getLastChangePassword()!=null) {
			return getLastChangePassword().getTime();
		}
		return 0;
	}
	
	public Actor toActor() {
		Actor actor=new Actor();
		actor.setActorId(getId());
		actor.setActorName(getUsername());
		actor.setActorFullName(getFullName());
		return actor;
	}
}

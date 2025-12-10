package ws.core.model.embeded;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public class FirstReview {
	@Field(value = "createdTime")
	private Date createdTime;
	
	@Indexed
	@Field(value = "reviewed")
	private boolean reviewed;
	
	@Field(value = "reviewedTime")
	private Date reviewedTime;
	
	@Field(value = "choiceOrganizationId")
	private String choiceOrganizationId;
	
	@Field(value = "choiceOrganizationGroupId")
	private String choiceOrganizationGroupId;
	
	@Field(value = "choiceOrganizationRoleId")
	private String choiceOrganizationRoleId;
	
	public FirstReview() {
		this.createdTime=new Date();
		this.reviewed=false;
	}
	
	public long getCreatedTimeLong() {
		if(getCreatedTime()!=null)
			return getCreatedTime().getTime();
		return 0;
	}
	
	public long getReviewedTimeLong() {
		if(getReviewedTime()!=null)
			return getReviewedTime().getTime();
		return 0;
	}
}

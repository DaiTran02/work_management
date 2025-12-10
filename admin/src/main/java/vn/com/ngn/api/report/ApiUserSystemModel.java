package vn.com.ngn.api.report;

import java.util.List;

import lombok.Data;
import vn.com.ngn.utils.LocalDateUtils;

@Data
public class ApiUserSystemModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String username;
	private String email;
	private String phone;
	private String fullName;
	private Object jobTitle;
	private String creatorId;
	private String creatorName;
	private boolean active;
	private String activeCode;
	private long lastDateLogin;
	private long lastChangePassword;
	private Object lastIPLogin;
	private List<Object> belongOrganizations;

	@Data
	public class BelongOrganization{
		private String organizationId;
		private String organizationName;
	}
	
	
	public String getJobTitleString() {
		
		if(jobTitle == null)
			return "Chưa cập nhật";
		
		return jobTitle.toString().isEmpty() ? "Chưa cập nhật" : jobTitle.toString();
	}
	
	public String getStatusString() {
		return active == false ? "Không hoạt động" : "Hoạt động";
	}
	
	public String getPhoneNumberString() {
		
		if(phone == null )
			return "Chưa cập nhật";
		
		return phone.isEmpty() ? "Chưa cập nhật" : phone;
	}
	
	public String getCreateTimeString() {
		return LocalDateUtils.dfDate.format(createdTime);
	}
	
	public String getActiveText() {
		return active == false ? "Không hoạt động" : "Hoạt động";
	}
}

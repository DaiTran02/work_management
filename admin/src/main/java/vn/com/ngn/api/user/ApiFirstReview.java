package vn.com.ngn.api.user;

import lombok.Data;

@Data
public class ApiFirstReview {
	private long createdTime;
	private String choiceOrganizationId;
	private Object choiceOrganizationGroupId;
	private Object choiceOrganizationRoleId;
	private boolean reviewed;
	private long reviewedTime;
	
	public ApiFirstReview() {
		
	}
	
	public ApiFirstReview(ApiFirstReview api) {
		this.createdTime = api.createdTime;
		this.choiceOrganizationId = api.getChoiceOrganizationId();
		this.choiceOrganizationGroupId = api.getChoiceOrganizationGroupId();
		this.choiceOrganizationRoleId = api.getChoiceOrganizationRoleId();
		this.reviewed = api.isReviewed();
		this.reviewedTime = api.getReviewedTime();
	}
}

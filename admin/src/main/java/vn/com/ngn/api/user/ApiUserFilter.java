package vn.com.ngn.api.user;

import lombok.Data;

@Data
public class ApiUserFilter {
	private int skip = 0;
	private int limit = 0;
	private String keyword;
	private boolean active;
	private String excludeOrganizationId;
	private String includeOrganizationId;
	private String hasFirstReview;
	private String firstReviewed;
	private String provider;
	private String hasUsed;
}

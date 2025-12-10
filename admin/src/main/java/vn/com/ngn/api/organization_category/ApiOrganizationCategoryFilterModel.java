package vn.com.ngn.api.organization_category;

import lombok.Data;

@Data
public class ApiOrganizationCategoryFilterModel {
	private int skip;
	private int limit;
	private String keyword;
	private boolean active;

}

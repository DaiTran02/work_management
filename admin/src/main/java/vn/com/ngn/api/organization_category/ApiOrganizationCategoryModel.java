package vn.com.ngn.api.organization_category;

import lombok.Data;

@Data
public class ApiOrganizationCategoryModel {
	private String id;
	private String name;
	private String description;
	private boolean active;
	private int order;
	private int count;
}

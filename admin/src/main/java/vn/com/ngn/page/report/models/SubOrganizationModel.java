package vn.com.ngn.page.report.models;

import java.util.List;

import lombok.Data;

@Data
public class SubOrganizationModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String name;
	private String description;
	private String creatorId;
	private String creatorName;
	private String path;
	private String parentId;
	private List<String> parentIdSeconds;
	private boolean active;
	private int order;
	private String unitCode;
	private int countSubOrganization;
	private List<SubOrganizationModel> subOrganizations;
	private List<UserOrganizationModel> userOrganizationExpands;
}

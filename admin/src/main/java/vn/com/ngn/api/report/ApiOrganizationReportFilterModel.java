package vn.com.ngn.api.report;

import lombok.Data;

@Data
public class ApiOrganizationReportFilterModel {
	private String parentId;
	private int skip = 0;
	private int limit = 0;
	private String keyword;
	private boolean active = true;
}

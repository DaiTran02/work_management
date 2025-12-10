package vn.com.ngn.api.report;

import lombok.Data;

@Data
public class ApiFilterReportOrgSystemModel { 
	private int skip;
	private int limit;
	private String used;
	private String active;
}

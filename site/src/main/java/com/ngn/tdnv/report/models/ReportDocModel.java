package com.ngn.tdnv.report.models;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.report.ApiReportDocModel;
import com.ngn.api.report.ApiReportDocModel.ApiReportOwner;
import com.ngn.api.utils.ApiKeyValueModel;
import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class ReportDocModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String number;
	private String symbol;
	private ApiKeyValueModel security;
	private long regDate;
	private String type;
	private String signerName;
	private String signerPosition;
	private int copies;
	private int pages;
	private String orgReceiveName;
	private String orgCreateName;
	private String summary;
	private Owner owner;
	private ApiKeyValueModel category;
	private Object external;
	private List<String> attachments;
	private String creatorId;
	private String creatorName;
	private int countTask;
	private boolean active;
	private boolean trash;
	private ApiKeyValueModel status;
	private String classifyTaskId;
	private String leaderApproveTaskId;
	
	public ReportDocModel() {
		
	}
	
	public ReportDocModel(ApiReportDocModel apiReportDocModel) {
		ModelMapper mapper = new ModelMapper();
		owner = new Owner(apiReportDocModel.getOwner());
		mapper.map(apiReportDocModel, this);
	}
	
	
	@Data
	public class Owner{
		private String organizationId;
		private String organizationName;
		private String organizationGroupId;
		private String organizationGroupName;
		private String organizationUserId;
		private String organizationUserName;
		
		public Owner() {
			
		}
		
		public Owner(ApiReportOwner apiOwner) {
			this.organizationId = apiOwner.getOrganizationId();
			this.organizationName = apiOwner.getOrganizationName();
			this.organizationGroupId = apiOwner.getOrganizationGroupId();
			this.organizationGroupName = apiOwner.getOrganizationGroupName();
			this.organizationUserId = apiOwner.getOrganizationUserId();
			this.organizationUserName = apiOwner.getOrganizationUserName();
		}
		
	}
	

	public String getCountTaskText() {
		return countTask == 0 ? "Chưa giao" : "Đã giao";
	}
	
	public String getRegDayText() {
		return LocalDateUtil.dfDate.format(this.getRegDate());
	}
	
	public String getCreateTimeText() {
		return LocalDateUtil.dfDate.format(this.getCreatedTime());
	}
	
}

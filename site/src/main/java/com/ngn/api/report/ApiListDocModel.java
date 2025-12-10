package com.ngn.api.report;

import java.util.List;

import com.ngn.api.utils.ApiKeyValueModel;

import lombok.Data;

@Data
public class ApiListDocModel {
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
	private Object owner;
	private ApiKeyValueModel category;
	private Object external;
	private List<String> attachments;
	private String creatorId;
	private String creatorName;
	private boolean active;
	private boolean trash;
	private ApiKeyValueModel status;
	private String classifyTaskId;
	private String leaderApproveTaskId;
	
	@Data
	public class Owner{
		private String organizationId;
		private String organizationName;
		private String organizationGroupId;
		private String organizationGroupName;
		private String organizationUserId;
		private String organizationUserName;
	}
}

package com.ngn.tdnv.doc.models;

import java.util.List;

import com.ngn.api.doc.ApiDocOrgModel;

import lombok.Data;

@Data
public class DocInputModel {
	private String number;
	private String symbol;
	private String security;
	private long regDate;
	private String type;
	private String signerName;
	private String signerPosition;
	private String copies;
	private String pages;
	private String orgReceiveName;
	private String orgCreateName;
	private String summary;
	private String category;
	private boolean active;
	private Owner owner;
	private List<ApiDocOrgModel> receivers;
	private List<String> attachments;
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

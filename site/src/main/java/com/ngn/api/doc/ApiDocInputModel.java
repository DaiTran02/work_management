package com.ngn.api.doc;

import java.util.ArrayList;
import java.util.List;

import com.ngn.tdnv.doc.models.DocInputModel;

import lombok.Data;

@Data
public class ApiDocInputModel {
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
	private ApiOwner owner;
	private List<ApiDocOrgModel> receivers = new ArrayList<ApiDocOrgModel>();
	private List<String> attachments;
	private String classifyTaskId;
	private String leaderApproveTaskId;
	
	public ApiDocInputModel() {
		
	}
	
	public ApiDocInputModel(DocInputModel docInputModel) {
		this.number = docInputModel.getNumber();
		this.symbol = docInputModel.getSymbol();
		this.security = docInputModel.getSecurity();
		this.regDate = docInputModel.getRegDate();
		this.type = docInputModel.getType();
		this.signerName = docInputModel.getSignerName();
		this.signerPosition = docInputModel.getSignerPosition();
		this.copies = docInputModel.getCopies();
		this.pages = docInputModel.getPages();
		this.orgReceiveName = docInputModel.getOrgReceiveName();
		this.orgCreateName = docInputModel.getOrgCreateName();
		this.summary = docInputModel.getSummary();
		this.category = docInputModel.getCategory();
		this.active = docInputModel.isActive();
		this.attachments = docInputModel.getAttachments();
		this.owner = new ApiOwner(docInputModel.getOwner());
		this.classifyTaskId = docInputModel.getClassifyTaskId();
		this.leaderApproveTaskId = docInputModel.getLeaderApproveTaskId();
		if(docInputModel.getReceivers() != null && !docInputModel.getReceivers().isEmpty()) {
			docInputModel.getReceivers().forEach(model->{
				this.receivers.add(model);
			});
		}
	}

	@Data
	public class ApiOwner{
		private String organizationId;
		private String organizationName;
		private String organizationGroupId;
		private String organizationGroupName;
		private String organizationUserId;
		private String organizationUserName;
		
		public ApiOwner() {
			
		}
		
		public ApiOwner(DocInputModel.Owner owner) {
			this.organizationId = owner.getOrganizationId();
			this.organizationName = owner.getOrganizationName();
			this.organizationGroupId = owner.getOrganizationGroupId();
			this.organizationGroupName = owner.getOrganizationGroupName();
			this.organizationUserId = owner.getOrganizationUserId();
			this.organizationUserName = owner.getOrganizationUserName();
		}
		
	}
}

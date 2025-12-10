package com.ngn.tdnv.doc.models;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.doc.ApiDocModel.ApiOwnerModel;
import com.ngn.api.doc.ApiDocOrgModel;
import com.ngn.api.doc.ApiDocResultConfirmModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.models.KeyValueModel;

import lombok.Data;

@Data
public class DocModel {
	private String id;
    private long createdTime;
    private long updatedTime;
    private String number;
    private String symbol;
    private KeyValueModel security;
    private long regDate;
    private String type;
    private String signerName;
    private String signerPosition;
    private int copies;
    private int pages;
    private String orgReceiveName;
    private String orgCreateName;
    private String summary;
    private OwnerModel owner;
    private List<ApiDocOrgModel> receivers;
    private KeyValueModel category;
    private Object external;
    private List<String> attachments;
    private String creatorId;
    private String creatorName;
    private boolean active;
    private boolean trash;
    private KeyValueModel status;
    private String classifyTaskId;
    private String leaderApproveTaskId;
    private int countTask;
    private int countTaskCompleted;
    private String iOfficeId;
    private String source;
    private ApiDocResultConfirmModel resultConfirm;
    
    public DocModel() {
    	
    }
    
    public DocModel(ApiDocModel apiDocModel) {
    	this.owner = new OwnerModel(apiDocModel.getOwner());
    	ModelMapper mapper = new ModelMapper();
    	mapper.map(apiDocModel, this);
    	if(apiDocModel.getReceivers() != null) {
    		receivers = new ArrayList<ApiDocOrgModel>();
    		apiDocModel.getReceivers().forEach(model->{
    			ApiDocOrgModel apiDocOrgModel = model;
    			receivers.add(apiDocOrgModel);
    		});
    	}
    }
    
    @Data
    public class OwnerModel{
    	private String organizationId;
    	private String organizationName;
    	private Object organizationCode;
    	private String organizationGroupId;
    	private String organizationGroupName;
    	private String organizationUserId;
    	private String organizationUserName;
    	
    	public OwnerModel() {
    		
    	}
    	
    	public OwnerModel(ApiOwnerModel apiOwnerModel) {
    		this.organizationId = apiOwnerModel.getOrganizationId();
    		this.organizationName = apiOwnerModel.getOrganizationName();
    		this.organizationCode = apiOwnerModel.getOrganizationCode();
    		this.organizationGroupId = apiOwnerModel.getOrganizationGroupId();
    		this.organizationGroupName = apiOwnerModel.getOrganizationGroupName();
    		this.organizationUserId = apiOwnerModel.getOrganizationUserId();
    		this.organizationUserName = apiOwnerModel.getOrganizationUserName();
    	}
    }
    
    public String getCreateTimeText() {
    	return createdTime == 0 ? "" : LocalDateUtil.dfDateTime.format(createdTime);
    }
    
    public String getUpdateTimeText() {
    	return updatedTime == 0 ? "Chưa cập nhật" : LocalDateUtil.dfDateTime.format(updatedTime);
    }
    
    public String getRegTimeText() {
    	return regDate == 0 ? "Đang cập nhật" : LocalDateUtil.dfDateTime.format(regDate);
    }
    
    public String getOrgReceiveNameText() {
    	return orgReceiveName.isEmpty() ? "Chưa cập nhật" : orgReceiveName;
    }
    
    public String getOrgCreateNameText() {
    	return orgCreateName.isEmpty() ? "Chưa cập nhật" : orgCreateName;
    }
    
    public String getCountTaskText() {
    	return countTask == 0 ? "Văn bản chưa giao nhiệm vụ nào" : String.valueOf(countTask);
    }
    
    public String getIOfficeIdText() {
    	return iOfficeId == null ? "Không có" : iOfficeId;
    }
    
}

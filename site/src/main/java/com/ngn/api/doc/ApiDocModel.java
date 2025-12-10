package com.ngn.api.doc;

import java.util.List;

import com.ngn.api.utils.ApiKeyValueModel;

import lombok.Data;

@Data
public class ApiDocModel {
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
    private ApiOwnerModel owner;
    private List<ApiDocOrgModel> receivers;
    private Object external;
    private List<String> attachments;
    private String creatorId;
    private String creatorName;
    private boolean active;
    private boolean trash;
    private ApiKeyValueModel status;
    private ApiKeyValueModel category;
    private String classifyTaskId;
    private String leaderApproveTaskId;
    private int countTask;
    private int countTaskCompleted;
    private String iOfficeId;
    private String source;
    private ApiDocResultConfirmModel resultConfirm;
    
    @Data
    public class ApiOwnerModel{
    	private String organizationId;
    	private String organizationName;
    	private Object organizationCode;
    	private String organizationGroupId;
    	private String organizationGroupName;
    	private String organizationUserId;
    	private String organizationUserName;
    }
}

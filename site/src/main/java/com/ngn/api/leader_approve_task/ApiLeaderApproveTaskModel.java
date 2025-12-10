package com.ngn.api.leader_approve_task;

import lombok.Data;

@Data
public class ApiLeaderApproveTaskModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String name;
	private String organizationId;
	private String organizationName;
	private int order;
	private boolean active;
	private Creator creator;
    
    @Data
    public class Creator{
        private String organizationId;
        private String organizationName;
        private String organizationUserId;
        private String organizationUserName;
    }
}

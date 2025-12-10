package com.ngn.setting.leader_classify.model;

import org.modelmapper.ModelMapper;

import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskModel;

import lombok.Data;

@Data
public class LeaderApproveTaskModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String name;
	private String organizationId;
	private String organizationName;
	private int order;
	private boolean active;
	private Creator creator = new Creator();

	@Data
	public class Creator{
		private String organizationId;
		private String organizationName;
		private String organizationUserId;
		private String organizationUserName;
		
		public Creator() {
			
		}
		
		public Creator(ApiLeaderApproveTaskModel.Creator creator) {
			ModelMapper mapper = new ModelMapper();
			mapper.map(creator, LeaderApproveTaskModel.Creator.class);
		}
		
		
	}
	
	public LeaderApproveTaskModel() {
		
	}
	
	public LeaderApproveTaskModel(ApiLeaderApproveTaskModel apiLeaderApproveTaskModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiLeaderApproveTaskModel,this);
	}
	
}

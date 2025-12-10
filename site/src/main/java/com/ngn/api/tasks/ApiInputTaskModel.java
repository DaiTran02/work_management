package com.ngn.api.tasks;

import java.util.ArrayList;
import java.util.List;

import com.ngn.tdnv.task.models.TaskOutputModel;

import lombok.Data;

@Data
public class ApiInputTaskModel {
	private String docId;
	private String parentId;
	private ApiOrgGeneralOfTaskModel owner;
	private ApiOrgGeneralOfTaskModel assistant;
	private ApiOrgGeneralOfTaskModel assignee;
	private List<ApiOrgGeneralOfTaskModel> supports;
	private List<ApiFollowerModel> followers;
	private String priority;
	private String title;
	private String description;
	private long createTime;
	private long endTime;
	private boolean requiredConfirm;
	private boolean requiredKpi;
	private String classifyTaskId;
	private String leaderApproveTaskId;
	private ApiOrgGeneralOfTaskModel creator;
	private List<String> attachments = new ArrayList<String>();
	
	public ApiInputTaskModel() {
		
	}
	
	public ApiInputTaskModel(TaskOutputModel outputTaskModel) {
		this.docId = outputTaskModel.getDocId();
		this.parentId = outputTaskModel.getParentId();
		this.owner = new ApiOrgGeneralOfTaskModel(outputTaskModel.getOwner());
		this.assistant = new ApiOrgGeneralOfTaskModel(outputTaskModel.getAssistant());
		this.assignee = new ApiOrgGeneralOfTaskModel(outputTaskModel.getAssignee());
		this.supports = outputTaskModel.getSupports().stream().map(ApiOrgGeneralOfTaskModel::new).toList();
		this.followers = outputTaskModel.getFollowers().stream().map(ApiFollowerModel::new).toList();
		this.priority = outputTaskModel.getPriority();
		this.title = outputTaskModel.getTitle();
		this.description = outputTaskModel.getDescription();
		this.endTime = outputTaskModel.getEndTime();
		this.createTime = outputTaskModel.getCreatedTime();
		this.requiredConfirm = outputTaskModel.isRequiredConfirm();
		this.classifyTaskId = outputTaskModel.getClassifyTaskId();
		this.leaderApproveTaskId = outputTaskModel.getLeaderApproveTaskId();
		this.requiredKpi = outputTaskModel.isRequiredKpi();
		outputTaskModel.getAttachments().forEach(model->{
			attachments.add(model.toString());
		});
		
	}
	
}

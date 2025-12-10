package com.ngn.api.tasks;

import java.util.List;

import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.tasks.actions.ApiPedingModel;

import lombok.Data;

@Data
public class ApiOutputTaskModel {
	private String id;
	private long createdTime;
	private long updatedTime;
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
	private long endTime;
	private long startingTime;
	private boolean requiredConfirm;
	private ApiTaskReportedModel reported;
	private ApiTaskCompletedModel completed;
	private Object state;
	private Object status;
	private int countSubTask;
	private List<Object> attachments;
	private List<ApiProcessModel> processes;
	private List<ApiTaskCommentModel> comments;
	private List<ApiTaskRemindModel> reminds;
	private List<ApiEventModel> events;
	private ApiTaskRateModel rating;
	private ApiTaskRedoModel redo;
	private List<ApiTaskRedoModel> redoHistories;
	private ApiPedingModel pending;
	private List<ApiPedingModel> pendingHistories;
	private Object notify;
	private ApiDocInforOfTaskModel docInfo;
	private List<Object> docReferences;
	private Object syncSourceExternal;
	private String classifyTaskId;
	private String leaderApproveTaskId;
	private ApiTaskRefuseConfirmModel confirmRefuse;
	private List<ApiTaskRefuseConfirmModel> confirmRefuseHisories;
	private ApiOutputRefuseModel refuse;
	private List<Object> refuseHistories;
	private ApiTaskReverseModel reverse;
	private List<Object> reverseHistories;
	private ApiDocModel docModel;
	private int countSubTaskIsCompleted;
	private boolean requiredKpi;
}

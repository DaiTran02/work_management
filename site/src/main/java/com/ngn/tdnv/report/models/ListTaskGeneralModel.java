package com.ngn.tdnv.report.models;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.report.ApiReportSupportModel;

import lombok.Data;

import com.ngn.api.report.ApiListTaskAssigneeModel;
import com.ngn.api.report.ApiListTaskAssigneeModel.Assignee;
import com.ngn.api.report.ApiListTaskAssigneeModel.Assistant;
import com.ngn.api.report.ApiListTaskAssigneeModel.DocInfo;
import com.ngn.api.report.ApiListTaskAssigneeModel.Owner;

@Data
public class ListTaskGeneralModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String docId;
	private Object parentId;
	private Owner owner;
	private Assistant assistant;
	private Assignee assignee;
	private List<ApiReportSupportModel> supports;
	private List<Object> follower;
	private String priority;
	private String title;
	private String description;
	private long endTime;
	private long startingTime;
	private boolean requiredConfirm;
	private Object reported;
	private Object completed;
	private String status;
	private int countSubTask;
	public List<Object> attachments;
	public List<Object> processes;
	public List<Object> comments;
	public List<Object> reminds;
	public List<Object> events;
	public Object rating;
	public Object redo;
	public List<Object> redoHistories;
	public Object pending;
	public List<Object> pendingHistories;
	public Object notify;
	public DocInfo docInfo;
	public List<Object> docReferences;
	public Object syncSourceExternal;
	
	public ListTaskGeneralModel() {
		
	}
	
	public ListTaskGeneralModel(ApiListTaskAssigneeModel apiListTaskAssigneeModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiListTaskAssigneeModel, this);
	}
	
	public ListTaskGeneralModel(ApiReportSupportModel apiReportSupportModel) {
		ModelMapper mapper = new ModelMapper();
		mapper.map(apiReportSupportModel, this);
	}
}

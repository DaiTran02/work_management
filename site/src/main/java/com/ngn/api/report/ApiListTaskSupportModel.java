package com.ngn.api.report;

import java.util.Date;
import java.util.List;

import com.ngn.api.tasks.ApiFollowerModel;
import com.ngn.api.tasks.ApiProcessModel;
import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class ApiListTaskSupportModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String docId;
	private Object parentId;
	private Owner owner;
	private Assistant assistant;
	private Assignee assignee;
	private List<ApiReportSupportModel> supports;
	private List<ApiFollowerModel> follower;
	private String priority;
	private String title;
	private String description;
	private long endTime;
	private long startingTime;
	private boolean requiredConfirm;
	private Object reported;
	private Completed completed;
	private String status;
	private int countSubTask;
	public List<Object> attachments;
	public List<ApiProcessModel> processes;
	public List<Object> comments;
	public List<Object> reminds;
	public List<Object> events;
	public Rating rating;
	public Object redo;
	public List<Object> redoHistories;
	public Object pending;
	public List<Object> pendingHistories;
	public Object notify;
	public DocInfo docInfo;
	public List<Object> docReferences;
	public Object syncSourceExternal;


	@Data
	public class Assignee {
		private String organizationId;
		private String organizationName;
		private Object organizationUserId;
		private Object organizationUserName;
	}

	@Data
	public class Assistant{
		private String organizationId;
		private String organizationName;
		private String organizationGroupId;
		private String organizationGroupName;
		private String organizationUserId;
		private String organizationUserName;
	}

	@Data
	public class Comment{
		private String id;
		private Date createdTime;
		private Object updatedTime;
		private String message;
		private Creator creator;
		private List<Object> attachments;
		private List<Reply> replies;
		private ObjectId objectId;
		private Object createdTimeLong;
		private int updatedTimeLong;
	}

	@Data
	public class Creator{
		private String organizationId;
		private String organizationName;
		private String organizationUserId;
		private String organizationUserName;
	}

	@Data
	public class ObjectId {
		private int timestamp;
		private Date date;
	}

	@Data
	public class Reply {
		private String id;
		private Date createdTime;
		private Object updatedTime;
		private String message;
		private Creator creator;
		private List<Object> attachments;
		private List<Object> replies;
		private ObjectId objectId;
		private Object createdTimeLong;
		private int updatedTimeLong;
	}

	@Data
	public class Owner{
		private String organizationId;
		private String organizationName;
		private String organizationUserId;
		private String organizationUserName;
	}

	@Data
	public class Follower {
		private String organizationId;
		private String organizationName;
		private String organizationGroupId;
		private String organizationGroupName;
		private Object organizationUserId;
		private Object organizationUserName;
	}

	@Data
	public class Event {
		private String id;
		private Object createdTime;
		private String action;
		private String title;
		private Object descriptions;
		private Creator creator;
	}

	@Data
	public class DocInfo{
		private String number;
		private String symbol;
		private String summary;
		private String category;
	}
	
	@Data
	public class Rating{
		private String id;
		private Long createdTime;
		private int star;
		private String explain;
		private Object creator;
	}
	
	@Data
	public class Completed{
		private String id;
		private long createdTime;
		private long confirmedTime;
		private long completedTime;
		private String completedStatus;
		private List<Object> attachments;
		private Object creator;
	}
	
	public String getEndTimeText() {
		return this.getEndTime() == 0 ? "Không hạn" : LocalDateUtil.dfDate.format(this.getEndTime());
	}
	
	public String getCreateTimeText() {
		return LocalDateUtil.dfDate.format(this.getCreatedTime());
	}
	
	public String getStartTimeText() {
		return this.startingTime == 0 ? "Nhiệm vụ chưa bắt đầu" : LocalDateUtil.dfDate.format(this.getStartingTime());
	}
	
	public String getExplainProcess() {
		return processes.isEmpty() ? "Đang trong quá trình thực hiện nhiệm vụ" : processes.get(0).getExplain();
	}
	
	public String getFollowerText() {
		return follower.isEmpty() ? "Không có" : follower.get(0).getOrganizationGroupName();
	}
	
	public String getAssigneeText() {
		return assignee.getOrganizationUserId() != null ? assignee.getOrganizationName()+"("+assignee.getOrganizationName()+")" : assignee.getOrganizationName();
	}
	
	public String getSupportText() {
		String supportText = "";
		if(supports.isEmpty()) {
			return "Không có";
		}else {
			for(ApiReportSupportModel apiReportSupportModel : supports) {
				supportText += apiReportSupportModel.getOrganizationName();
			}
		}
		return supportText;
	}

	public String getDocNumberText() {
		return docId == null ? "Nhiệm vụ không văn bản" : docInfo.getNumber();
	}
	
	public String getDocSymbolText() {
		return docId == null ? "Nhiệm vụ không văn bản" : docInfo.getSymbol();
	}
	
	public String getSummaryText() {
		return docId == null ? "" : "Trích yếu: "+docInfo.getSummary() +" / ";
	}
}

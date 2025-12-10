package com.ngn.tdnv.task.models;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.tasks.ApiOutputRefuseModel;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskRedoModel;
import com.ngn.api.tasks.ApiTaskRefuseConfirmModel;
import com.ngn.api.tasks.ApiTaskReportedModel;
import com.ngn.api.tasks.ApiTaskReverseModel;
import com.ngn.api.tasks.actions.ApiPedingModel;
import com.ngn.tdnv.task.enums.StatusTaskEnum;
import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class TaskOutputModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String docId;
	private String parentId;
	private TaskOrgGeneralModel owner;
	private TaskOrgGeneralModel assistant;
	private TaskOrgGeneralModel assignee;
	private List<TaskOrgGeneralModel> supports;
	private List<FollowerModel> followers;
	private String priority;
	private String title;
	private String description;
	private long endTime;
	private long startingTime;
	private boolean requiredConfirm;
	private ApiTaskReportedModel reported;
	private TaskCompletedModel completed;
	private Object state;
	private Object status;
	private int countSubTask;
	private List<Object> attachments;
	private List<TaskProcessModel> processes;
	private List<TaskCommentModel> comments;
	private List<TaskRemindModel> reminds;
	private List<TaskEventModel> events;
	private TaskRateModel rating;
	private ApiTaskRedoModel redo;
	private List<ApiTaskRedoModel> redoHistories;
	private ApiPedingModel pending;
	private List<ApiPedingModel> pendingHistories;
	private Object notify;
	private TaskDocInforModel docInfo = new TaskDocInforModel();
	private List<Object> docReferences;
	private Object syncSourceExternal;
	private String classifyTaskId;
	private String leaderApproveTaskId;
	private ApiOutputRefuseModel refuse;
	private List<Object> refuseHistories;
	private ApiTaskReverseModel reverse;
	private List<Object> reverseHistories;
	private ApiTaskRefuseConfirmModel confirmRefuse;
	private List<ApiTaskRefuseConfirmModel> confirmRefuseHisories;
	private boolean requiredKpi;
	ModelMapper mapper = new ModelMapper();
	
	public TaskOutputModel() {
		
	}
	
	public TaskOutputModel(ApiOutputTaskModel apiOutputTaskModel) {
		this.reverse = apiOutputTaskModel.getReverse();
		this.redo = apiOutputTaskModel.getRedo();
		mapper.map(apiOutputTaskModel, this);
	}
	
	
	public String getEndTimeText() {
		return endTime == 0 ? "Không hạn" : LocalDateUtil.dfDateTime.format(endTime);
	}
	
	public String getStartTimeText() {
		return startingTime == 0 ? "Chưa thực hiện" : LocalDateUtil.dfDateTime.format(startingTime);
	}
	
	public String getCountFollowerText() {
		return followers == null ? "0" : String.valueOf(followers.size());
	}
	
	public String getCalculateStartTimeText() {
		if(startingTime == 0) {
			return "Nhiệm vụ chưa bắt đầu thực hiện";
		}
		
		LocalDateTime startTimeLong = LocalDateUtil.longToLocalDateTime(startingTime);
		LocalDateTime now = LocalDateTime.now();
		
		if(state.equals(StatusTaskEnum.DAHOANTHANH.getKey())) {
			LocalDateTime completedTime = LocalDateUtil.longToLocalDateTime(completed.getCompletedTime());
			Long days = calculateBetweenDayUseDuration(startTimeLong, completedTime);
			
			if(days == 0) {
				Long hours = calculateBetweenHours(startTimeLong,completedTime);
				
				if(hours == 0) {
					Long minutes = calculateBetweenMinutes(startTimeLong, completedTime);
					return minutes + " phút";
				}else {
					return hours + " giờ";
				}
			}else {
				return days + " ngày";
			}
		}else {
			Long days = calculateBetweenDayUseDuration(startTimeLong, now);
			
			if(days == 0) {
				Long hours = calculateBetweenHours(startTimeLong, now);
				
				if(hours == 0) {
					Long minutes = calculateBetweenMinutes(startTimeLong, now);
					return minutes + " phút";
				}else {
					return hours + " giờ";
				}
			}else {
				return days + " ngày";
			}
		}
	}
	
	public String getCompleteTimeText() {
		return LocalDateUtil.dfDateTime.format(completed.getCompletedTime());
	}
	
	public String getCalculateCompleteTimeText() {
		long result = calculateBetweenDays(LocalDateUtil.longToLocalDate(endTime), LocalDateUtil.longToLocalDate(completed.getCompletedTime()));
		long result2 = Math.abs(result);
		return String.valueOf(result2) +" ngày";
	}
	
	public String getCreateTimeText() {
		return createdTime == 0 ? "Đang kiểm tra" : LocalDateUtil.dfDateTime.format(createdTime);
	}
	
	public String getCalculateCreateTimeText() {
		
		LocalDateTime createDateTimeLong = LocalDateUtil.longToLocalDateTime(createdTime);
		LocalDateTime now = LocalDateTime.now();
		
		Long days = calculateBetweenDayUseDuration(createDateTimeLong, now);
		
		if(days == 0) {
			Long hours = calculateBetweenHours(createDateTimeLong,now);
			
			if(hours == 0) {
				Long minutes = calculateBetweenMinutes(createDateTimeLong, now);
				return minutes +" phút";
			}else {
				return hours + " giờ";
			}
		}else {
			return days + " ngày";
		}
//		
//		String result = String.valueOf(calculateBetweenDays(LocalDateUtil.longToLocalDate(createdTime), LocalDate.now()) == 0 ? calculateBetweenHours(LocalDateUtil.longToLocalDateTime(createdTime)) + "Giờ"
//				: calculateBetweenDays(LocalDateUtil.longToLocalDate(createdTime), LocalDate.now())+"Ngày");
//		return result;
	}
	
	public String getCalculateTimeRemainText() {
		if(endTime == 0) {
			return "Không hạn";
		}
		LocalDateTime localDateEndTime = LocalDateUtil.longToLocalDateTime(endTime);
		Duration duration = Duration.between(LocalDateTime.now(), localDateEndTime);
		String timeRemind = "";
		long days = duration.toDays();
		long hours = 0;
		long minutes = 0;
		if(days == 0) {
			hours = duration.toHours();
			if(hours < 0) {
				minutes = duration.toMinutes();
				if(minutes < 0) {
					timeRemind += "Hết hạn";
				}else {
					timeRemind += minutes + " phút (Sắp hết hạn)";
				}
			}else {
				timeRemind += hours + " giờ (Sắp hết hạn)";
			}
		}else if(days < 0){
			timeRemind += "Đã hết hạn ("+Math.abs(checkOverDue())+" ngày)";
		}else {
			if(days >= 0 && days <= 3) {
				timeRemind += days + " ngày (Sắp hết hạn)";
			}else {
				timeRemind += days + " ngày";
			}
		}
		return timeRemind;
	}
	
	private long calculateBetweenDays(LocalDate before,LocalDate after) {
		return ChronoUnit.DAYS.between(before, after);
	}
	
	public long calculateBetweenDayUseDuration(LocalDateTime before,LocalDateTime after) {
		return Duration.between(before, after).toDays();
	}
	
	public long checkOverDue() {
		return Duration.between(LocalDateTime.now(), LocalDateUtil.longToLocalDateTime(endTime)).toDays();
	}
	
	private long calculateBetweenHours(LocalDateTime createDateTime,LocalDateTime after) {
		return Duration.between(createDateTime, after).toHours();
	}
	
	private long calculateBetweenMinutes(LocalDateTime before,LocalDateTime after) {
		return Duration.between(before, after).toMinutes();
	}
	
	public String getProcessText() {
		return processes == null ? "0" : String.valueOf(processes.size());
	}
}

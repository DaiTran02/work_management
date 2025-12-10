package com.ngn.api.report;

import java.util.List;

import lombok.Data;

@Data
public class ApiReportKpiModel {
	private int markA;
	private int markB;
	private int markC;
	private int totalPercent;
	private int totalMark;
	private int taskCompleted;
	private int taskNotCompleted;
	private int taskCompletedButThroughExpired;
	private int taskCompletedButNotThroughExpired;
	private int taskIsRatedHigherThanThreeStars;
	private List<ApiListTasksOwnerModel> listTasks;
}

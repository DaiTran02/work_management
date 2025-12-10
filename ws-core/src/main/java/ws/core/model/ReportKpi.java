package ws.core.model;

import java.util.List;

import org.bson.Document;

import lombok.Data;

@Data
public class ReportKpi {
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
	private List<Document> listTasks;
}

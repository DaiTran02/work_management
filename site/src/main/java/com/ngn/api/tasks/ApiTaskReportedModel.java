package com.ngn.api.tasks;

import java.util.List;

import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class ApiTaskReportedModel {
	private String id;
    private long createdTime;
    private long completedTime;
    private String reportedStatus;
    private List<String> attachments;
    private ApiCreatorModel creator;
    
    public String getCompletedTimeText() {
    	return completedTime == 0 ? "" : LocalDateUtil.dfDateTime.format(completedTime);
    }
}

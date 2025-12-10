package com.ngn.api.tasks;

import java.util.List;

import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class ApiTaskReverseModel {
	private String id;
	private long createdTime;
    private String reasonReverse;
    private List<String> attachments;
    private ApiCreatorModel creator;
    
    public String getCreatedTimeText() {
    	return createdTime == 0 ? "Đang kiểm tra":LocalDateUtil.dfDateTime.format(createdTime);
    }
}

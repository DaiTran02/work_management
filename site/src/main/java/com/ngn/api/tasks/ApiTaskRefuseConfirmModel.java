package com.ngn.api.tasks;

import java.util.List;

import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class ApiTaskRefuseConfirmModel {
	private String id;
	private Long createdTime;
    private String reasonConfirmRefuse;
    private List<String> attachments;
    private ApiCreatorModel creator;
    
    public String getCreateTimeText() {
    	return createdTime == null ? "Đang cập nhật" : LocalDateUtil.dfDateTime.format(createdTime);
    }
}

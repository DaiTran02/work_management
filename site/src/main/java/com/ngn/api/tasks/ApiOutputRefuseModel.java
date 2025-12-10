package com.ngn.api.tasks;

import java.util.List;

import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class ApiOutputRefuseModel {
	private String id;
	private long createdTime;
	private String reasonRefuse;
	private List<String> attachments;
	private ApiCreatorModel creator;
	
	public String getCreatedTimeText() {
		return createdTime == 0 ? "Đang cập nhật" : LocalDateUtil.dfDateTime.format(createdTime);
	}
}

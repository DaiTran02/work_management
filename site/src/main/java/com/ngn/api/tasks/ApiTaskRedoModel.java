package com.ngn.api.tasks;

import java.util.List;

import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class ApiTaskRedoModel {
	private String id;
	private long createdTime;
	private String reasonRedo;
	private List<String> attachments;
	private ApiCreatorModel creator;
	
	public String getCreatedTimeText() {
		return createdTime == 0 ? "Đang cập nhật" : LocalDateUtil.dfDateTime.format(createdTime);
	}
}

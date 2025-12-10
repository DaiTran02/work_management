package com.ngn.api.tasks.actions;

import java.util.List;

import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class ApiPedingModel {
	private String id;
	private long createdTime;
	private String reasonPending;
	private List<String> attachments;
	private ApiCreatorActionModel creator;
	
	public String getCreatedTimeText() {
		return LocalDateUtil.dfDateTime.format(createdTime);
	}
	
	public String getAttachmentText() {
		if(attachments.isEmpty() || attachments == null) {
			return "0";
		}
		return attachments.size()+"";
	}
}

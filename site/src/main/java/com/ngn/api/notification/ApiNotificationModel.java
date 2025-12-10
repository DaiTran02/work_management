package com.ngn.api.notification;

import java.util.List;

import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class ApiNotificationModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String title;
	private String content;
	private ApiNotifiKeyValueModel type;
	private ApiNotifiActionModel action;
	private Object actionUrl;
	private ApiNotifiKeyValueModel object;
	private String objectId;
	private Object creator;
	private ApiNotifiInfoOrgModel receiver;
	private boolean viewed;
	private long viewedTime;
	private ApiNotifiKeyValueModel scope;
	private List<Object> metaDatas;
	
	public String getCreatedTimeText() {
		return LocalDateUtil.dfDateTime.format(createdTime);
	}
	
	public String getViewedTimeText() {
		return viewedTime == 0 ? "" : LocalDateUtil.dfDateTime.format(viewedTime);
	}
}

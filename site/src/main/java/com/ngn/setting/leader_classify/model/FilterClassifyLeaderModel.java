package com.ngn.setting.leader_classify.model;

import lombok.Data;

@Data
public class FilterClassifyLeaderModel {
	private int skip = 0;
	private int limit = 10;
	private String keyword;
	private boolean active;
	private String organizationId;

}

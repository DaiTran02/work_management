package com.ngn.api.tasks;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ApiTaskSummaryModel {
	private String key;
	private String name;
	private String shortName;
	private int count;
	private List<ApiTaskSummaryModel> child = new ArrayList<ApiTaskSummaryModel>();
}

package com.ngn.api.tags;

import java.util.List;

import lombok.Data;

@Data
public class ApiTagModel {
	private String id;
	private String name;
	private String color;
	private boolean active;
	private String type;
	private List<String> classIds;
	private ApiTagCreatetorModel creator;
}

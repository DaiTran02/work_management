package com.ngn.api.tags;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ApiInputAddclassModel {
	private String tagId;
	private List<String> classIds = new ArrayList<String>();
	private String type;
	private ApiTagCreatetorModel creator;
	
	public ApiInputAddclassModel() {
		
	}
	
	public ApiInputAddclassModel(ApiTagModel apiTagModel) {
		this.tagId = apiTagModel.getId();
		this.classIds.addAll(apiTagModel.getClassIds());
		this.type = apiTagModel.getType();
		this.creator = apiTagModel.getCreator();
	}
}

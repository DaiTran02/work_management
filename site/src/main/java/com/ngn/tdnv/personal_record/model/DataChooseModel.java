package com.ngn.tdnv.personal_record.model;

import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class DataChooseModel {
	private String id;
	private String name;
	private String type;
	private String typeId;
	private String createTime;

	public DataChooseModel() {
		
	}
	
	public DataChooseModel(ApiDocModel apiDocModel) {
		this.id = apiDocModel.getId();
		this.name = apiDocModel.getSummary();
		this.type = "Văn bản";
		this.typeId = "Doc";
		this.createTime = LocalDateUtil.dfDate.format(apiDocModel.getCreatedTime());
	}
	
	public DataChooseModel(ApiOutputTaskModel apiOutputTaskModel) {
		this.id = apiOutputTaskModel.getId();
		this.name = apiOutputTaskModel.getTitle();
		this.type = "Nhiệm vụ";
		this.typeId = "Task";
		this.createTime = LocalDateUtil.dfDate.format(apiOutputTaskModel.getCreatedTime());
	}
}

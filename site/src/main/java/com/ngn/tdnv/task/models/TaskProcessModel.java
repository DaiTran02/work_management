package com.ngn.tdnv.task.models;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.ngn.api.tasks.ApiProcessModel;
import com.ngn.utils.LocalDateUtil;

import lombok.Data;

@Data
public class TaskProcessModel {
	  private String id;
	  private long createdTime;
	  private int percent;
	  private String explain;
	  private TaskCreatorModel creator;
	  private List<String> attachments = new ArrayList<String>();
	  private long createdTimeLong;
	  
	  public TaskProcessModel () {
		  
	  }
	  
	  public TaskProcessModel(ApiProcessModel apiProcessModel) {
		  ModelMapper mapper = new ModelMapper();
		  mapper.map(apiProcessModel, this);
	  }
	  
	  public String getCreateTimeText() {
		  return createdTime == 0 ? "Chưa cập nhật" : LocalDateUtil.dfDateTime.format(createdTime);
	  }
	  
	  public String getExplainText() {
		  return explain == null ? "Chưa cập nhật" : explain;
	  }
}

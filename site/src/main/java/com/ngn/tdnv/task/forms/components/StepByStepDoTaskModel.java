package com.ngn.tdnv.task.forms.components;

import lombok.Data;

@Data
public class StepByStepDoTaskModel {
	private String header;
	private String helper;
	private boolean isActive;
	private boolean isDone;
	
	public StepByStepDoTaskModel() {
		
	}
	
	public StepByStepDoTaskModel(String header,String helper,boolean isActive,boolean isDone) {
		this.header = header;
		this.helper = helper;
		this.isActive = isActive;
		this.isDone = isDone;
	}
	
}

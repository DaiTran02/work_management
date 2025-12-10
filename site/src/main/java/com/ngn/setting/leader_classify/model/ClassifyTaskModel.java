package com.ngn.setting.leader_classify.model;

import org.modelmapper.ModelMapper;

import com.ngn.api.classify_task.ApiClassifyTaskModel;

import lombok.Data;

@Data
public class ClassifyTaskModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String name;
	private String organizationId;
	private String organizationName;
	private int order;
	private boolean active;
	private Creator creator = new Creator();

	@Data
	public class Creator{
		private String organizationId;
		private String organizationName;
		private String organizationUserId;
		private String organizationUserName;
		
		public Creator() {
			
		}
		
		public Creator(ApiClassifyTaskModel.Creator creator) {
			ModelMapper mapper = new ModelMapper();
			mapper.map(creator, ClassifyTaskModel.Creator.class);
		}
		
	}

	public ClassifyTaskModel() {

	}

	public ClassifyTaskModel(ApiClassifyTaskModel apiClassifyTaskModel) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.map(apiClassifyTaskModel, this);
	}

}

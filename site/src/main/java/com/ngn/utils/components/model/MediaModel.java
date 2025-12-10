package com.ngn.utils.components.model;

import lombok.Data;

@Data
public class MediaModel {
	private String id;
	private long createdTime;
	private long updatedTime;
	private String fileName;
	private String fileDescription;
	private String fileType;
	private int fileSize;
	private String filePath;
	private Creator creator;

	@Data
	public class Creator{
		private String organizationId;
		private String organizationName;
		private String organizationUserId;
		private String organizationUserName;
	}

}

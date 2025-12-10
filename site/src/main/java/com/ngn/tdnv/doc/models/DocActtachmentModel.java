package com.ngn.tdnv.doc.models;

import java.util.Date;

import com.ngn.api.doc.ApiDocAttachmentModel;

import lombok.Data;

@Data
public class DocActtachmentModel {
	private String id;
	private Date createdTime;
	private Date updatedTime;
	private String fileName;
	private String fileDescription;
	private String fileType;
	private int fileSize;
	private String filePath;
	private Object creator;
	private Object external;
	private Object createdTimeLong;
	private Object updatedTimeLong;
	
	public DocActtachmentModel() {
		
	}
	
	public DocActtachmentModel(ApiDocAttachmentModel apiDocAttachmentModel) {
		this.id = apiDocAttachmentModel.getId();
		this.createdTime = apiDocAttachmentModel.getCreatedTime();
		this.updatedTime = apiDocAttachmentModel.getUpdatedTime();
		this.fileName = apiDocAttachmentModel.getFileName();
		this.fileDescription = apiDocAttachmentModel.getFileDescription();
		this.fileType = apiDocAttachmentModel.getFileType();
		this.fileSize = apiDocAttachmentModel.getFileSize();
		this.creator = apiDocAttachmentModel.getCreator();
		this.external = apiDocAttachmentModel.getExternal();
		this.filePath = apiDocAttachmentModel.getFilePath();
		this.createdTimeLong = apiDocAttachmentModel.getCreatedTimeLong();
		this.updatedTimeLong = apiDocAttachmentModel.getUpdatedTimeLong();
	}
}

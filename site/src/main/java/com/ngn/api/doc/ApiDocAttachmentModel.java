package com.ngn.api.doc;

import java.util.Date;

import lombok.Data;

@Data
public class ApiDocAttachmentModel {
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
}

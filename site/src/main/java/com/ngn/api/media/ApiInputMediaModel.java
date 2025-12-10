package com.ngn.api.media;

import org.springframework.core.io.FileSystemResource;

import lombok.Data;

@Data
public class ApiInputMediaModel {
	private FileSystemResource file;
	private String description;
	private String organizationId;
	private String organizationName;
	private String organizationUserId;
	private String organizationUserName;

}

package com.ngn.api.utils;

import org.springframework.core.io.FileSystemResource;

import lombok.Data;

@Data
public class ApiFileModel {
	private FileSystemResource file;
	private String description;

}

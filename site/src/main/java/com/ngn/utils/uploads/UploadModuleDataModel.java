package com.ngn.utils.uploads;

import java.io.InputStream;

import lombok.Data;

@Data
public class UploadModuleDataModel {
	protected String fileName;
	protected String fileType;
	protected InputStream inputStream;
	protected String description;
}

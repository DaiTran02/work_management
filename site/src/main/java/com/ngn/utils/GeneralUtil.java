package com.ngn.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import com.vaadin.flow.server.StreamResource;

public class GeneralUtil {
	public static byte[] base64ToByteArray(String base64) throws IOException {
		try {
			byte[] decodedBytes = Base64.getMimeDecoder().decode(base64);
			return decodedBytes;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static InputStream byteArrayToInputStream(byte[] bytes) throws IOException {
		InputStream inputStream = new ByteArrayInputStream(bytes);
		
		return inputStream;
	}
	
	public static StreamResource getStreamResource(String filename, byte[] content) {
        return new StreamResource(filename,
                () -> new ByteArrayInputStream(content));
    }

}

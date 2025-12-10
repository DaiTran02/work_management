package com.ngn.utils.components;

import java.io.IOException;

import com.ngn.interfaces.FormInterface;
import com.ngn.utils.GeneralUtil;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.server.StreamResource;

public class ViewAttachmentForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	PdfViewer pdfViewer = new PdfViewer();
	
	private String fileName;
	private String base64;
	public ViewAttachmentForm(String fileName,String base64) {
		this.fileName = fileName;
		this.base64 = base64;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		pdfViewer.setSizeFull();
		
		this.add(pdfViewer);
		
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		StreamResource streamResource;
		try {
			streamResource = GeneralUtil.getStreamResource(fileName, GeneralUtil.base64ToByteArray(base64));
			pdfViewer.setSrc(streamResource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}















package com.ngn.utils.components;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.ngn.interfaces.FormInterface;
import com.vaadin.flow.component.html.IFrame;

public class ViewFileDocxForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private IFrame iFrame = new IFrame();
	
	private String fileName;
	private String base64;
	public ViewFileDocxForm(String fileName,String base64) {
		this.fileName = fileName;
		this.base64 = base64;
		
		buildLayout();
		configComponent();
		loadData();
	}
	
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		iFrame.setSizeFull();
		this.add(iFrame);
	}

	@Override
	public void configComponent() {
		System.out.println(fileName);
		
	}
	
	private void loadData() {
		iFrame.getElement().setProperty("srcdoc", convertBase64ToDocToHtml(base64));
	}
	
	private String convertBase64ToDocToHtml(String base64) {
		byte[] decodeDoc = Base64.getDecoder().decode(base64);
		String htmlContent = "";
		try(XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(decodeDoc));
				XWPFWordExtractor extractor = new XWPFWordExtractor(doc)){
			 String docText = extractor.getText();
	         htmlContent = "<html><body>" + docText + "</body></html>";
	         
		}catch(Exception e) {
			
		}
		
		return htmlContent;
		
	}

}

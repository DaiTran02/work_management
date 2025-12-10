package com.ngn.utils.uploads;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ngn.utils.components.NotificationTemplate;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.shared.Registration;

import elemental.json.Json;
import elemental.json.impl.JreJsonString;
import lombok.Data;

public class UploadModuleBasic extends UploadModule{
	private static final long serialVersionUID = 1L;

	private List<String> listFileName = new ArrayList<String>();
	private Map<String, TextField> mapFileUpload = new HashMap<String, TextField>();
	private VerticalLayout vLayout = new VerticalLayout();

	public void initUpload() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		super.buildLayout();
		this.add(vLayout);
	}

	@Override
	public void configComponent() {
		super.configComponent();
		


		upload.addSucceededListener(e->{
			TextField txtField = new TextField();
			if(checkFileMatchName(e.getFileName())) {
				removeCurrentFile(upload, this.bufferSingle);
			}else {
				listFileName.add(e.getFileName());
				mapFileUpload.put(e.getFileName(), txtField);
				initTxtDers();
			}
			
		});
		
		this.addFileRemoveListener(e->{
			listFileName.remove(e.getFileName());
			mapFileUpload.remove(e.getFileName());
			initTxtDers();
		});
	}
	
	private void initTxtDers() {
		vLayout.setWidthFull();
		vLayout.removeAll();
		for(Map.Entry<String, TextField> m : mapFileUpload.entrySet() ) {
			TextField txtDers = m.getValue();
			txtDers.setWidthFull();
			txtDers.setLabel("Mô tả của "+m.getKey());
			txtDers.addThemeVariants(TextFieldVariant.LUMO_SMALL);
			vLayout.add(txtDers);
		}
		
	}

	Registration addFileRemoveListener(ComponentEventListener<FileRemoveEvent> listener) {
		return super.addListener(FileRemoveEvent.class, listener);
	}

	@DomEvent("file-remove")
	public static class FileRemoveEvent extends ComponentEvent<UploadModuleBasic> {
		private static final long serialVersionUID = 1L;
		private String fileName;

		public FileRemoveEvent(UploadModuleBasic source, boolean fromClient, @EventData("event.detail.file.name") JreJsonString fileNameJson) {
			super(source, fromClient);
			fileName = fileNameJson.getString();
		}

		public String getFileName() {
			return fileName;
		}
	}

	@Override
	public void loadDisplay() {
	}

	public List<UploadModuleDataModel> getListFileUpload(){
		List<UploadModuleDataModel> list = new ArrayList<UploadModuleDataModel>();

		for(String fileName : bufferMulti.getFiles()) {
			if (listFileName.contains(fileName)) {
				String fileType = bufferMulti.getFileData(fileName).getMimeType();
				InputStream inputStream = bufferMulti.getInputStream(fileName);

				UploadModuleDataModel modelFile = new UploadModuleDataModel();
				modelFile.setFileName(fileName);
				modelFile.setFileType(fileType);
				modelFile.setInputStream(inputStream);
				modelFile.setDescription(mapFileUpload.get(fileName).getValue().toString());

				list.add(modelFile);
			}
		}
		

		return list;
	}
	
	private boolean checkFileMatchName(String fileName) {
		for(String name : listFileName) {
			if(name.equals(fileName)) {
				return true;
			}
		}
		return false;
	}
	

	public void clear() {
		listFileName.clear();
		upload.getElement().setPropertyJson("files", Json.createArray());
		bufferMulti.getFiles().clear();
	}
	
	private void removeCurrentFile(Upload upload, MemoryBuffer buffer) {
		NotificationTemplate.error("Tên tệp đã tồn tại, vui lòng đổi tên hoặc chọn tệp khác");
	    buffer.getInputStream();
	    upload.getElement().executeJs(
	            "this.files = Array.from(this.files).filter(file => file !== this.files[this.files.length - 1]);");
	}


	
	@Data
	public class FileNameValicator implements ValueChangeListener<HasValue.ValueChangeEvent<String>>{
		private static final long serialVersionUID = 1L;
		
		private List<String> listFile = new ArrayList<String>();

		@Override
		public void valueChanged(ValueChangeEvent<String> event) {
			String fileName = event.getValue();
			
			if(!isValidName(fileName)) {
				NotificationTemplate.error("damnn");
			}
			
		}
		
		private boolean isValidName(String fileName) {
			for(String name : listFile) {
				if(name.equals(fileName)) {
					return false;
				}
			}
			return true;
		}
		
	}

}

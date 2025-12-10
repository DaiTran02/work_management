package com.ngn.tdnv.task.forms.details;

import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.uploads.UploadModuleBasic;
import com.ngn.utils.uploads.UploadModuleDataModel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

public class TaskRemidForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	
	private TextArea txtDesr = new TextArea("Nội dung nhắc nhở");
	private ButtonTemplate btnConfirm = new ButtonTemplate("Nhắc nhở",FontAwesome.Solid.BELL.create());
	private UploadModuleBasic uploadModuleBasic = new UploadModuleBasic();
	public TaskRemidForm() {
		buildLayout();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.setPadding(false);
		this.getStyle().setPaddingTop("0");
		this.add(vLayout);
		createLayout();
	}

	@Override
	public void configComponent() {
	}
	
	private void createLayout() {
		txtDesr.setWidthFull();
		txtDesr.setHeight("200px");
		txtDesr.getStyle().setPadding("0");
		
		btnConfirm.getStyle().setMarginLeft("auto");
		
		uploadModuleBasic.initUpload();
		
		vLayout.add(txtDesr,uploadModuleBasic);
		vLayout.setSizeFull();
		vLayout.setPadding(false);
	}
	
	public String getReasonRemind() {
		return this.txtDesr.getValue();
	}
	
	public List<UploadModuleDataModel> getListUpload(){
		return this.uploadModuleBasic.getListFileUpload();
	}
	
	

}

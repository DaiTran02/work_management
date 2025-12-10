package com.ngn.utils.components;

import java.util.ArrayList;
import java.util.List;

import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.model.CustomModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;

public class CustomView extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	List<CustomModel> list = new ArrayList<CustomModel>();
	List<TextField> lis = new ArrayList<TextField>();
	
	public CustomView() {
		buildLayout();
		configComponent();
		
		for(int i=0;i<10;i++) {
			CustomModel customModel = new CustomModel();
			customModel.setId("1");
			customModel.setNameComponent("textfield");
			customModel.setType("textfield");
			list.add(customModel);
		}
		
		this.add(custom());
		Button btn = new Button("Bam");
		btn.addClickListener(e->{
			lis.stream().forEach(model->{
				if(model instanceof TextField) {
					System.out.println(model.getValue());
				}
			});
		});
		this.add(btn);
	}
	
	@Override
	public void buildLayout() {
		
	}

	@Override
	public void configComponent() {
		
	}
	
	public Component custom() {
		
		FlexLayout flexLayout = new FlexLayout();
		
		
		for(CustomModel customModel : list) {
			
			switch(customModel.getType()) {
			case "textfield":
				TextField txtF = new TextField(customModel.getNameComponent());
				flexLayout.add(txtF);
				lis.add(txtF);
				break;
				
			case "combobox":
				break;
			}
			
		}
		
		return flexLayout;
		
	}
	

}

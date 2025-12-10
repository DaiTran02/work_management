package com.ngn.utils.custom_field;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;

public class CustomFieldForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private VerticalLayout vLayout = new VerticalLayout();
	private ComboBox<Pair<String, String>> cmbTypeOfComponent = new ComboBox<Pair<String,String>>("Chọn loại");
	
	public CustomFieldForm() {
		loadTypeOfComponent();
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(cmbTypeOfComponent);
		this.add(vLayout);
		vLayout.setWidthFull();
	}

	@Override
	public void configComponent() {
		cmbTypeOfComponent.addValueChangeListener(e->{
			loadLayout();
		});
	}
	
	private void loadTypeOfComponent() {
		List<Pair<String, String>> listTypes = new ArrayList<Pair<String,String>>();
		listTypes.add(Pair.of("txt","Ô nhập"));
		listTypes.add(Pair.of("cmb","Ô chọn"));
		
		cmbTypeOfComponent.setItems(listTypes);
		cmbTypeOfComponent.setItemLabelGenerator(Pair::getValue);
	}
	
	private void loadLayout() {
		vLayout.removeAll();
		vLayout.getStyle().setBoxShadow("rgba(0, 0, 0, 0.16) 0px 1px 4px").setPadding("5px");
		
		H5 header = new H5("Cấu hình");
		
		header.getStyle().setMargin("auto");
		
		vLayout.add(header,new Hr());
		
		VerticalLayout vLayoutLeft = new VerticalLayout();
		vLayoutLeft.add(new H5("Form"),new Hr());
		vLayoutLeft.setHeightFull();
		
		VerticalLayout vLayoutRight = new VerticalLayout();
		vLayoutRight.add(new H5("Properties"), new Hr());
		vLayoutRight.setHeightFull();
		
		SplitLayout splitLayout = new SplitLayout(vLayoutLeft,vLayoutRight);
		splitLayout.setSizeFull();
		splitLayout.setSplitterPosition(70);
		
		
		vLayout.add(splitLayout);
		
		if(cmbTypeOfComponent.getValue().getKey().equals("txt")) {
			TextField txtField = new TextField();
			
			vLayoutLeft.add(txtField);
			
			TextField txtLabel = new TextField("Tên");
			txtLabel.setWidthFull();
			txtLabel.addValueChangeListener(e->{
				txtField.setLabel(txtLabel.getValue());
			});
			
			
			vLayoutRight.add(txtLabel);
			
		}
	}

}

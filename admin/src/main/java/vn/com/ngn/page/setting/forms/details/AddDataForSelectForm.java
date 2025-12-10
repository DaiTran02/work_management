package vn.com.ngn.page.setting.forms.details;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class AddDataForSelectForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	private ButtonTemplate btnAdd = new ButtonTemplate("Thêm",FontAwesome.Solid.PLUS.create());
	
	private Map<String, HorizontalLayout> mapComponets = new HashMap<String, HorizontalLayout>();
	private Map<String, Pair<TextField, TextField>> mapData = new HashMap<String, Pair<TextField,TextField>>();
	
	public AddDataForSelectForm() {
		buildLayout();
		configComponent();
		initComponent();
		createLayout();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		btnAdd.getStyle().set("margin-left", "auto");
		
		this.add(btnAdd,vLayout);
	}


	@Override
	public void configComponent() {
		btnAdd.addClickListener(e->{
			initComponent();
			createLayout();
		});
		
	}
	
	private void createLayout() {
		vLayout.removeAll();
		for(Map.Entry<String, HorizontalLayout> m : mapComponets.entrySet()) {
			vLayout.add(m.getValue(),new Hr());
		}
		
		Component[] components = vLayout.getChildren().toArray(Component[]::new);
		vLayout.remove(components[components.length-1]);
	}
	
	
	
	private int count = 0;
	private void initComponent() {
		HorizontalLayout hLayoutItem = new HorizontalLayout();
		
		TextField txtKey = new TextField("Key");
		TextField txtValue = new TextField("Value");
		ButtonTemplate btnRemove = new ButtonTemplate(FontAwesome.Solid.REMOVE.create());
		btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR);
		btnRemove.getStyle().set("margin-top", "29px");
		btnRemove.setId(String.valueOf(count));
		
		txtKey.setWidth("49%");
		txtValue.setWidth("49%");
		btnRemove.setWidth("2%");
		
		hLayoutItem.setWidthFull();
		hLayoutItem.add(txtKey,txtValue,btnRemove);
		
		btnRemove.addClickListener(e->{
			removeItem(btnRemove.getId().get());
			createLayout();
		});
		
		mapComponets.put(String.valueOf(count), hLayoutItem);
		mapData.put(String.valueOf(count), Pair.of(txtKey,txtValue));
		count++;
	}
	
	private void removeItem(String id) {
		if(mapComponets.containsKey(id)) {
			mapComponets.remove(id);
			mapData.remove(id);
		}
		
	}

	public List<Pair<String, String>> getData(){
		List<Pair<String, String>> list = new ArrayList<Pair<String,String>>();
		for(Map.Entry<String,Pair<TextField, TextField>> m : mapData.entrySet()) {
			list.add(Pair.of(m.getValue().getKey().getValue(),m.getValue().getValue().getValue()));
		}
		
		return list;
	}


}

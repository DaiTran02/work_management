package vn.com.ngn.page.setting.forms.details;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.setting.model.ComponentModel;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class ListComponentsForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private List<ComponentModel> listComponents = new ArrayList<ComponentModel>(); 
	private Grid<ComponentModel> gridComponent = new Grid<ComponentModel>(ComponentModel.class,false);
	private VerticalLayout vLayout = new VerticalLayout();
	
	private List<ComponentModel> listComponentIsChoose = new ArrayList<ComponentModel>();
	
	
	public ListComponentsForm() {
		loadList();
		buildLayout();
		configComponent();
		renderComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
	}

	@Override
	public void configComponent() {
		
	}
	
	private void renderComponent() {
		gridComponent = new Grid<ComponentModel>(ComponentModel.class,false);
		gridComponent.setItems(listComponents);
		gridComponent.addComponentColumn(ComponentModel::getComponent).setHeader("Thành phần");
		gridComponent.addComponentColumn(model->{
			Checkbox cbChoose = new Checkbox();
			
			cbChoose.addClickListener(e->{
				if(cbChoose.getValue() == true) {
					listComponentIsChoose.add(model);
				}else {
					listComponentIsChoose.remove(model);
				}
			});
			
			return cbChoose;
		}).setWidth("50px").setFlexGrow(0);
		
		gridComponent.setAllRowsVisible(true);
		gridComponent.addThemeVariants(GridVariant.LUMO_COMPACT);
		gridComponent.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		gridComponent.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		gridComponent.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		
		vLayout.removeAll();
		vLayout.add(gridComponent);
	}
	
	private void loadList() {
		listComponents = new ArrayList<ComponentModel>();
		ButtonTemplate btn = new ButtonTemplate("Ví dụ");
		btn.setWidthFull();
		listComponents.add(new ComponentModel(btn,"button"));
		
//		listComponents.add(new ComponentModel(new Checkbox("Ví dụ"), "checkbox"));
		
		TextField txt = new TextField("Ví dụ thanh nhập");
		txt.setWidthFull();
		
		listComponents.add(new ComponentModel(txt,"textfield"));
		
		Select<String> select = new Select<>();
		select.setLabel("Lựa chọn");
		select.setItems("Lựa chọn 1", "Lựa chọn 2");
		select.setValue("Lựa chọn 1");
		select.setWidthFull();
		
		listComponents.add(new ComponentModel(select,"select"));
		
	}
	
	public List<ComponentModel> getlistComponent(){
		return this.listComponentIsChoose;
	}

}

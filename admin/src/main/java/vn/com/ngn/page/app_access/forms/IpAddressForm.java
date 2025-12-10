package vn.com.ngn.page.app_access.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.app_access.models.GridComponentModel;

public class IpAddressForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	private int count = 0;
	
	private boolean checkListIp = false;
	
	private Button btnAddComponent = new Button("Thêm địa chỉ IP",FontAwesome.Solid.PLUS.create());
	
	private List<GridComponentModel> listModel = new ArrayList<GridComponentModel>();
	private Grid<GridComponentModel> grid = new Grid<GridComponentModel>(GridComponentModel.class,false);
	
	private List<TextField> listIpAddress = new ArrayList<TextField>();
	
	private Map<Integer, Component> listMapComponent = new HashMap<Integer, Component>();
	private List<String> listIp;
	public IpAddressForm(List<String> listIp) {
		buildLayout();
		configComponent();
		if(listIp.isEmpty()) {
			initComponent("");
			refreshData();
		}else {
			checkListIp = true;
			this.listIp = listIp;
			loadData();
		}
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.setPadding(false);
		
		btnAddComponent.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAddComponent.getStyle().setCursor("pointer");
		
		this.add(btnAddComponent,createGrid());
	}

	@Override
	public void configComponent() {
		btnAddComponent.addClickListener(e->{
			initComponent("");
			refreshData();
		});
	}
	
	public void loadData() {
		for(String iString : listIp) {
			initComponent(iString);
		}
		refreshData();
	}
	
	private Component createGrid() {
		grid = new Grid<GridComponentModel>(GridComponentModel.class,false);
		
		grid.addComponentColumn(model->{
			Div div = new Div();
			div.add(model.getComponent());
			div.setSizeFull();
			return div;
		}).setHeader("Địa chỉ IP");
		grid.addComponentColumn(model->{
			Button btnDelete = new Button(FontAwesome.Solid.CLOSE.create());
			btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnDelete.getStyle().setCursor("pointer");
			btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnDelete.setTooltipText("Xóa địa chỉ IP");
			btnDelete.addClickListener(e->{
				deleteComponent(model.getId());
			});
			return btnDelete;
		}).setHeader("Thao tác").setWidth("80px").setFlexGrow(0);
		
		grid.setSizeFull();
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		
		
		return grid;
	}
	
	private void refreshData() {
		listModel.clear();
		for(Map.Entry<Integer, Component> m : listMapComponent.entrySet()) {
			GridComponentModel gridComponentModel = new GridComponentModel();
			gridComponentModel.setId(m.getKey());
			gridComponentModel.setComponent(m.getValue());
			listModel.add(gridComponentModel);
		}
		grid.setItems(listModel);
		setListData();
	}
	
	private void initComponent(String value) {
		TextField txtIpAddress = new TextField();
		txtIpAddress.setSizeFull();
		txtIpAddress.setPlaceholder("xxx.xxx.xxx");
		
		if(checkListIp == true) {
			txtIpAddress.setValue(value);
		}
		
		listMapComponent.put(count, txtIpAddress);
		count++;
	}
	
	private void deleteComponent(int id) {
		for(Map.Entry<Integer, Component> m : listMapComponent.entrySet()) {
			if(id == m.getKey()) {
				listMapComponent.remove(m.getKey());
				break;
			}
		}
		refreshData();
		setListData();
	}
	
	private void setListData() {
		listIpAddress.clear();
		for(Map.Entry<Integer, Component> m : listMapComponent.entrySet()) {
			listIpAddress.add((TextField) m.getValue());
		}
	}
	
	public List<TextField> getListData(){
		return this.listIpAddress;
	}

}

















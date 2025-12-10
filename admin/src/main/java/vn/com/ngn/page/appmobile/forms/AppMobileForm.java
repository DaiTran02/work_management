package vn.com.ngn.page.appmobile.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.appmobile.ApiAppMobiModel;
import vn.com.ngn.api.appmobile.ApiAppMobiService;
import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.appmobile.models.AppMobiModel;
import vn.com.ngn.utils.components.PaginationForm;

public class AppMobileForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Grid<AppMobiModel> grid = new Grid<AppMobiModel>(AppMobiModel.class,false);
	private List<AppMobiModel> listModel = new ArrayList<AppMobiModel>();
	
	private PaginationForm paginationForm;
	
	public AppMobileForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		paginationForm = new PaginationForm(()->{
			if(paginationForm != null) {
				loadData();
			}
		});
		
		this.add(paginationForm,createGrid());
		
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiAppMobiModel>> listDevice = ApiAppMobiService.getListDevices(paginationForm.getSkip(),paginationForm.getLimit());
			paginationForm.setItemCount(listDevice.getTotal());
			listModel = listDevice.getResult().stream().map(AppMobiModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		grid.setItems(listModel);
	}
	
	private Component createGrid() {
		grid = new Grid<AppMobiModel>(AppMobiModel.class,false);
		
		grid.addColumn(AppMobiModel::getFullName).setHeader("Tên");
		grid.addColumn(AppMobiModel::getPhone).setHeader("Thiết bị");
		
		grid.setSizeFull();
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		return grid;
	}

}


























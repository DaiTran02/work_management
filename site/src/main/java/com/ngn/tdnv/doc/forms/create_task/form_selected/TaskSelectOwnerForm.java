package com.ngn.tdnv.doc.forms.create_task.form_selected;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.organization.ApiFilterOrgModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserOrganizationExModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.DetailsTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.provider.ListDataProvider;

//Form này dùng để chọn lãnh đạo giao nhiệm vụ từ cấp đơn vị xuống cấp phòng
public class TaskSelectOwnerForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Grid<Component> gridLayout = new Grid<Component>(Component.class,false);
	private List<Component> listComponents = new ArrayList<Component>();
	private ListDataProvider<Component> listDataProvider = new ListDataProvider<Component>(listComponents);
	private List<ApiOrganizationModel> listOrg = new ArrayList<ApiOrganizationModel>();
	private List<Checkbox> listCheckBoxUser = new ArrayList<Checkbox>();
	private Map<ApiOrganizationModel, ApiUserOrganizationExModel> mapOwnerSelected = new HashMap<ApiOrganizationModel, ApiUserOrganizationExModel>();
	
	private String idOrg;
	private Map<ApiOrganizationModel, ApiUserOrganizationExModel> mapOwner;
	public TaskSelectOwnerForm(String idOrg,Map<ApiOrganizationModel, ApiUserOrganizationExModel> mapOwner) {
		this.idOrg = idOrg;
		if(mapOwner != null) {
			this.mapOwner = mapOwner;
		}
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(gridLayout);
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		listOrg = new ArrayList<ApiOrganizationModel>();
		listCheckBoxUser.clear();
		
		//Lấy danh sách đơn vị cha và các đơn vị con
		ApiOrganizationModel apiOrganizationModel = getOrg(idOrg);
		if(apiOrganizationModel != null) {
			if(apiOrganizationModel.getLevel().getKey().equals("room")) {
				ApiOrganizationModel parentOrg = getOrg(apiOrganizationModel.getParentId());
				listOrg.add(parentOrg);
				List<ApiOrganizationModel> listSubOrg = getListSubOrg(parentOrg.getId());
				if(listSubOrg != null) {
					listOrg.addAll(listSubOrg);
				}
			}else {
				List<ApiOrganizationModel> listSubOrg = getListSubOrg(idOrg);
				ApiOrganizationModel currentOrg = getOrg(idOrg);
				listSubOrg.add(currentOrg);
				if(listSubOrg != null) {
					listOrg.addAll(listSubOrg);
				}
				Collections.reverse(listOrg);
			}
		}
		
		listComponents.clear();
		
		loadGridLayout();
		createLayout();
		loadDataGridLayout();
	}
	
	private void createLayout() {
		for(ApiOrganizationModel apiOrganizationModel : listOrg) {
			String nameOrg = idOrg.equals(apiOrganizationModel.getId()) == false ?  apiOrganizationModel.getName() : apiOrganizationModel.getName()+" (đơn vị hiện tại)";
			DetailsTemplate detailsTemplate = new DetailsTemplate(nameOrg, FontAwesome.Solid.HOME_ALT.create());
			
			detailsTemplate.setOpened(false);
			
			Grid<ApiUserOrganizationExModel> grid = new Grid<>(ApiUserOrganizationExModel.class,false);
			grid.addComponentColumn(model->{
				Checkbox cbUser = new Checkbox();
				cbUser.setId(model.getUserId());
				
				cbUser.addClickListener(e->{
					if(cbUser.getValue()) {
						mapOwnerSelected.clear();
						mapOwnerSelected.put(apiOrganizationModel, model);
						listCheckBoxUser.forEach(cb->{
							if(cb.getId().get().equals(model.getUserId())) {
								
							}else {
								cb.setValue(false);
							}
						});
					}else {
						mapOwnerSelected.remove(apiOrganizationModel);
					}
				});
				
				if(mapOwner != null) {
					mapOwner.forEach((k,v)->{
						if(k.getId().equals(apiOrganizationModel.getId())) {
							if(v != null) {
								if(v.getUserId().equals(model.getUserId())) {
									cbUser.setValue(true);
									mapOwnerSelected.put(apiOrganizationModel, model);
								}
							}
						}
					});
				}
				
				listCheckBoxUser.add(cbUser);
				return cbUser;
			}).setWidth("80px").setFlexGrow(0);
			
			grid.addColumn(ApiUserOrganizationExModel::getFullName).setHeader("Tên");
			grid.addColumn(ApiUserOrganizationExModel::getPositionName).setHeader("Chức vụ");
			if(apiOrganizationModel.getUserOrganizationExpands() != null) {
				grid.setItems(apiOrganizationModel.getUserOrganizationExpands());
			}
			
			grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
			grid.setAllRowsVisible(true);
			
			detailsTemplate.add(grid);
			
			if(mapOwner != null) {
				mapOwner.forEach((k,v)->{
					if(k.getId().equals(apiOrganizationModel.getId())) {
						detailsTemplate.setOpened(true);
					}
				});
			}
			
			listComponents.add(detailsTemplate);
		}
	}
	
	private void loadGridLayout() {
		gridLayout.addComponentColumn(model->{
			return model;
		});
	}
	
	private void loadDataGridLayout() {
		listDataProvider = new ListDataProvider<Component>(listComponents);
		gridLayout.setItems(listDataProvider);
	}
	
	public void save() {
		fireEvent(new ClickEvent(this, false));
	}
	
	private ApiOrganizationModel getOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(idOrg);
		if(data.isSuccess()) {
			return data.getResult();
		}
		return null;
	}
	
	private List<ApiOrganizationModel> getListSubOrg(String idOrg){
		ApiFilterOrgModel apiFilterOrgModel = new ApiFilterOrgModel();
		apiFilterOrgModel.setLimit(0);
		apiFilterOrgModel.setSkip(0);
		apiFilterOrgModel.setParentId(idOrg);
		ApiResultResponse<List<ApiOrganizationModel>> data = ApiOrganizationService.getListOrg(apiFilterOrgModel);
		if(data.isSuccess()) {
			return data.getResult();
		}
		return null;
	}

	public Map<ApiOrganizationModel, ApiUserOrganizationExModel> getMapOwnerSelected() {
		return mapOwnerSelected;
	}

}
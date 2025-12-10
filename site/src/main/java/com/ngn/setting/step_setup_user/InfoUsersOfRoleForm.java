package com.ngn.setting.step_setup_user;

import java.util.ArrayList;
import java.util.List;

import com.ngn.api.organization.ApiOrganizationServiceCustom;
import com.ngn.api.organization.ApiUsersRoleModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;

public class InfoUsersOfRoleForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Grid<ApiUsersRoleModel> grid = new Grid<ApiUsersRoleModel>(ApiUsersRoleModel.class,false);
	private List<ApiUsersRoleModel> listModel = new ArrayList<ApiUsersRoleModel>();
	
	private ApiOrganizationServiceCustom apiOrganizationServiceCustom;
	private String idOrg;
	private String idRole;
	public InfoUsersOfRoleForm(ApiOrganizationServiceCustom apiOrganizationServiceCustom,String idOrg,String idRole) {
		this.apiOrganizationServiceCustom = apiOrganizationServiceCustom;
		this.idOrg = idOrg;
		this.idRole = idRole;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createGrid());
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		listModel = new ArrayList<ApiUsersRoleModel>();
		ApiResultResponse<List<ApiUsersRoleModel>> data = apiOrganizationServiceCustom.getListUsersOfRole(idOrg, idRole);
		if(data.isSuccess()) {
			listModel.addAll(data.getResult());
		}
		
		grid.setItems(listModel);
	}
	
	private Component createGrid() {
		grid = new Grid<ApiUsersRoleModel>(ApiUsersRoleModel.class,false);
		
		grid.addColumn(model->{
			return model.getFullName();
		}).setHeader("Tên người sử dụng vai trò");
		
		
		return grid;
	}

}





























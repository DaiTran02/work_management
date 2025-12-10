package com.ngn.setting.step_setup_user;

import java.util.ArrayList;
import java.util.List;

import com.ngn.api.organization.ApiOrganizationServiceCustom;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;

public class InfoUserOfGroupForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Grid<ApiUserGroupExpandModel> grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);
	private List<ApiUserGroupExpandModel> listModel = new ArrayList<ApiUserGroupExpandModel>();
	
	private String idOrg;
	private String idGroup;
	private ApiOrganizationServiceCustom apiOrganizationServiceCustom;
	public InfoUserOfGroupForm(ApiOrganizationServiceCustom apiOrganizationServiceCustom,String idOrg,String idGroup) {
		this.apiOrganizationServiceCustom = apiOrganizationServiceCustom;
		this.idOrg = idOrg;
		this.idGroup = idGroup;
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
		listModel = new ArrayList<ApiUserGroupExpandModel>();
		ApiResultResponse<List<ApiUserGroupExpandModel>> dataUsers = apiOrganizationServiceCustom.getListUsersOfGroup(idOrg, idGroup);
		
		if(dataUsers.isSuccess()) {
			listModel.addAll(dataUsers.getResult());
		}
		
		grid.setItems(listModel);
		
	}
	
	private Component createGrid() {
		grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);
		
		grid.addColumn(model->{
			return model.getMoreInfo().getFullName();
		}).setHeader("Tên");
		
		grid.addColumn(model->{
			return model.getMoreInfo().getUsername();
		}).setHeader("Username");
		
		grid.addColumn(model->{
			return model.getPositonText();
		}).setHeader("Chức vụ");
		
		return grid;
	}

}





























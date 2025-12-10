package com.ngn.setting.components;

import java.util.ArrayList;
import java.util.List;

import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiOrganizationServiceCustom;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;

public class ListUserOfOrgForm extends VerticalLayoutTemplate{
	private static final long serialVersionUID = 1L;
	
	
	private final ApiOrganizationServiceCustom apiOrganizationServiceCustom;
	
	private Grid<ApiUserGroupExpandModel> grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);
	private List<ApiUserGroupExpandModel> listModel = new ArrayList<ApiUserGroupExpandModel>();
	
	
	private String orgId;
	public ListUserOfOrgForm(String orgId,ApiOrganizationServiceCustom apiOrganizationServiceCustom) {
		this.apiOrganizationServiceCustom = apiOrganizationServiceCustom;
		this.orgId = orgId;
		this.setSizeFull();
		this.add(createGrid());
		loadData();
	}
	
	public void loadData() {
		listModel = new ArrayList<ApiUserGroupExpandModel>();
		if(apiOrganizationServiceCustom != null) {
			ApiResultResponse<List<ApiUserGroupExpandModel>> data = apiOrganizationServiceCustom.getListUserOfOrg(orgId);
			if(data.isSuccess()) {
				listModel.addAll(data.getResult());
			}
		}else {
			ApiResultResponse<List<ApiUserGroupExpandModel>> data = ApiOrganizationService.getListUserOrganizationEx(orgId);
			if(data.isSuccess()) {
				listModel.addAll(data.getResult());
			}
		}
		
		grid.setItems(listModel);
	}
	
	private Grid<ApiUserGroupExpandModel> createGrid(){
		grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);
		
		grid.addComponentColumn(model->{
			Span span = new Span(model.getMoreInfo().getFullName());
			return span;
		}).setHeader("Tên");
		
		grid.addComponentColumn(model->{
			Span span = new Span(model.getPositonText());
			
			return span;
		}).setHeader("Chức vụ");
		
		grid.addColumn(model->{
			return model.getMoreInfo().getPhone();
		}).setHeader("Số điện thoại");
		
		grid.addColumn(model->{
			return model.getMoreInfo().getEmail();
		}).setHeader("Email");
		
		return grid;
	}

}

package com.ngn.setting.step_setup_user;

import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.organization.ApiGroupExpandModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationServiceCustom;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;

public class SecondStepChooseGroupForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private List<ApiGroupExpandModel> listModel = new ArrayList<ApiGroupExpandModel>();
	private Grid<ApiGroupExpandModel> grid = new Grid<ApiGroupExpandModel>(ApiGroupExpandModel.class,false);
	private ApiGroupExpandModel apiGroupChoose = new ApiGroupExpandModel();
	private List<Checkbox> listCheckbox = new ArrayList<Checkbox>();
	
	private ApiGroupExpandModel oldGroupModel = null;
	
	private ApiOrganizationServiceCustom apiOrganizationServiceCustom;
	private ApiOrganizationModel apiOrganizationModel;
	public SecondStepChooseGroupForm(ApiOrganizationServiceCustom apiOrganizationServiceCustom,ApiOrganizationModel apiOrganizationModel) {
		this.apiOrganizationModel = apiOrganizationModel;
		this.apiOrganizationServiceCustom = apiOrganizationServiceCustom;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		Span spanTitle = new Span("*Danh sách nhóm thuộc đơn vị "+apiOrganizationModel.getName()+". Chọn một nhóm trong đơn vị để làm việc (có thể bỏ qua nếu bạn không muốn vào nhóm). ");
		spanTitle.getStyle().setFontWeight(600);
		
		this.add(spanTitle,createGrid());
		
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		listModel = new ArrayList<ApiGroupExpandModel>();
		ApiResultResponse<List<ApiGroupExpandModel>> data = apiOrganizationServiceCustom.getListGroup(apiOrganizationModel.getId());
		if(data.isSuccess()) {
			listModel.addAll(data.getResult());
		}
		
		grid.setItems(listModel);
		
	}
	
	public void setOrgGroup(ApiGroupExpandModel oldGroupModel) {
		this.oldGroupModel = oldGroupModel;
		loadData();
	}
	
	private Component createGrid() {
		grid = new Grid<ApiGroupExpandModel>(ApiGroupExpandModel.class,false);
		
		grid.addComponentColumn(model->{
			Checkbox checkbox = new Checkbox();
			
			if(oldGroupModel != null && model.getGroupId().equals(oldGroupModel.getGroupId())) {
				apiGroupChoose = oldGroupModel;
				checkbox.setValue(true);
			}
			
			checkbox.addClickListener(e->{
				
				if(e.getSource().getValue() == true) {
					apiGroupChoose = model;
				}else {
					apiGroupChoose = null;
				}
				
				listCheckbox.forEach(cb->{
					if(!cb.equals(e.getSource())) {
						cb.setValue(false);
					}
				});
				
				fireEvent(new ClickEvent(this, false));
			});
			
			listCheckbox.add(checkbox);
			return checkbox;
		}).setWidth("50px").setFlexGrow(0);
		
		grid.addColumn(ApiGroupExpandModel::getName).setHeader("Tên nhóm");
		grid.addColumn(ApiGroupExpandModel::getDescription).setHeader("Mô tả");
		grid.addComponentColumn(model->{
			ButtonTemplate btnUsersOfGroup = new ButtonTemplate(model.getUserIds().size()+" người",FontAwesome.Solid.USERS.create());
			
			btnUsersOfGroup.addClickListener(e->{
				openDialogUsersOfGroup(model.getGroupId());
			});
			
			return btnUsersOfGroup;
		}).setHeader("Số thành viên trong nhóm");
		
		return grid;
	}
	
	private void openDialogUsersOfGroup(String idGroup) {
		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH NGƯỜI DÙNG THUỘC NHÓM");
		
		InfoUserOfGroupForm infoUserOfGroupForm = new InfoUserOfGroupForm(apiOrganizationServiceCustom, apiOrganizationModel.getId(), idGroup);
		dialogTemplate.add(infoUserOfGroupForm);
		
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setWidth("70%");
		dialogTemplate.setHeight("90%");
		dialogTemplate.open();
	}

	public ApiGroupExpandModel getApiGroupChoose() {
		return apiGroupChoose;
	}
	
	

}

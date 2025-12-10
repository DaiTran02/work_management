package com.ngn.tdnv.personal_record.form;

import java.util.ArrayList;
import java.util.List;

import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;

public class ChooseUserTransferForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	private List<ApiUserGroupExpandModel> listModel = new ArrayList<ApiUserGroupExpandModel>();
	private Grid<ApiUserGroupExpandModel> grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);
	private List<Checkbox> listCheckboxs = new ArrayList<Checkbox>();
	private ApiUserGroupExpandModel userIsChoose = new ApiUserGroupExpandModel();
	
	public ChooseUserTransferForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		H3 title = new H3("Chọn người dùng muốn chuyển giao hồ sơ");
		this.add(title,createGrid());
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		listModel = new ArrayList<ApiUserGroupExpandModel>();
		try {
			ApiResultResponse<List<ApiUserGroupExpandModel>> data = ApiOrganizationService.getListUserOrganizationEx(belongOrganizationModel.getOrganizationId());
			if(data.isSuccess()) {
				listModel.addAll(data.getResult());
			}
		} catch (Exception e) {
		}
		grid.setItems(listModel);
	}
	
	private Component createGrid() {
		grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);
		
		grid.addComponentColumn(model->{
			Checkbox cbChooseUser = new Checkbox();
			
			cbChooseUser.addClickListener(e->{
				userIsChoose = new ApiUserGroupExpandModel();
				listCheckboxs.forEach(cb->{
					if(!cb.equals(e.getSource())) {
						cb.setValue(false);
					}
				});
				if(cbChooseUser.getValue() == true) {
					userIsChoose = model;
				}
			});
			
			listCheckboxs.add(cbChooseUser);
			return cbChooseUser;
		}).setWidth("60px").setFlexGrow(0);
		grid.addColumn(ApiUserGroupExpandModel::getFullName).setHeader("Tên");
		return grid;
	}

	public ApiUserGroupExpandModel getUserIsChoose() {
		return userIsChoose;
	}

}

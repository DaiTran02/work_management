package com.ngn.tdnv.doc.forms.create_task.level_pass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.models.model_of_organization.OrganizationModel;
import com.ngn.utils.models.model_of_organization.UserOranizationExModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

//Chọn người dùng trong đơn vị
public class TaskChooseUserForOrgForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private boolean isUpdate = false;
	private TextField txtSearch = new TextField();
	private ButtonTemplate btnConfirm = new ButtonTemplate("Xác nhận");
	private ButtonTemplate btnClose = new ButtonTemplate("Đóng");
	private Span spNameOrg = new Span("");
	
	private Grid<UserOranizationExModel> grid = new Grid<UserOranizationExModel>(UserOranizationExModel.class,false);
	private List<UserOranizationExModel> listModel = new ArrayList<UserOranizationExModel>();
	
	private Map<OrganizationModel, UserOranizationExModel> mapUserSelected;
	
	private Map<OrganizationModel, UserOranizationExModel> mapUserIsChoose = new HashMap<OrganizationModel, UserOranizationExModel>();
	
	private List<Checkbox> listCheckbox = new ArrayList<Checkbox>();
	
	private OrganizationModel orgKey;
	
	public TaskChooseUserForOrgForm() {
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Nhập tên người dùng để tìm...");
		
		Span spNote = new Span("*Ghi chú: Nhiệm vụ sẽ được giao tới cấp người dùng chỉ định");
		
		HorizontalLayout hLayoutFilter = new HorizontalLayout();
		hLayoutFilter.setWidthFull();
		hLayoutFilter.setPadding(false);
		
		btnConfirm.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		
		hLayoutFilter.add(txtSearch,btnConfirm,btnClose);
		
		btnClose.addThemeVariants(ButtonVariant.LUMO_ERROR);
		
		this.add(spNote,hLayoutFilter,spNameOrg,createGrid());
	}

	@Override
	public void configComponent() {
		btnConfirm.addClickListener(e->fireEvent(new ClickEvent(this,false)));
	
	}
	
	
	public void loadData(OrganizationModel organizationModel,Map<OrganizationModel, UserOranizationExModel> userSelected) {
		listModel = new ArrayList<UserOranizationExModel>();
		orgKey = organizationModel;
		
		mapUserIsChoose = new HashMap<OrganizationModel, UserOranizationExModel>();
		
		spNameOrg.setText("*Đơn vị: "+organizationModel.getName());
		spNameOrg.getStyle().setFontWeight("600");
		
		this.mapUserSelected = userSelected;
		
		listModel.addAll(organizationModel.getUserOrganizationExpands());
		grid.setItems(listModel);
		checkToConfirm();
	}
	
	public void setIsUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
	
	private Component createGrid() {
		grid = new Grid<UserOranizationExModel>(UserOranizationExModel.class,false);
		
		grid.addComponentColumn(model->{
			Checkbox cbUser = new Checkbox();
			
			if(mapUserSelected.containsKey(orgKey)) {
				if(mapUserSelected.containsValue(model)) {
					mapUserIsChoose.put(orgKey, model);
					cbUser.setValue(true);
				}else {
					mapUserSelected.forEach((k,v)->{
						if(v.getUserId().equals(model.getUserId())) {
							mapUserIsChoose.put(orgKey, model);
							cbUser.setValue(true);
						}
					});
				}
			}
			
			if(isUpdate) {
				mapUserSelected.forEach((k,v)->{
					if(k.getId().equals(orgKey.getId())) {
						if(v.getUserId().equals(model.getUserId())) {
							mapUserIsChoose.put(orgKey, v);
							cbUser.setValue(true);
						}
					}
				});
			}
			
			cbUser.addClickListener(e->{
				if(mapUserIsChoose.containsKey(orgKey)) {
					mapUserIsChoose.remove(orgKey);
				}
				mapUserIsChoose.put(orgKey, model);
				listCheckbox.stream().forEach(cb->{
					if(!cb.equals(e.getSource())) {
						cb.setValue(false);
					}
				});
				checkToConfirm();
			});
			
			listCheckbox.add(cbUser);
			
			return cbUser;
		}).setWidth("50px").setFlexGrow(0);
		grid.addColumn(UserOranizationExModel::getFullName).setHeader("Tên");
		grid.addColumn(UserOranizationExModel::getPositionName).setHeader("Chức vụ");
		
		grid.setAllRowsVisible(true);
		grid.setSizeFull();
		
		return grid;
	}
	
	private void checkToConfirm() {
		if(mapUserIsChoose.isEmpty()) {
			btnConfirm.setEnabled(false);
		}else {
			btnConfirm.setEnabled(true);
		}
	}

	public ButtonTemplate getBtnClose() {
		return btnClose;
	}

	public Map<OrganizationModel, UserOranizationExModel> getMapUserIsChoose() {
		return mapUserIsChoose;
	}
}

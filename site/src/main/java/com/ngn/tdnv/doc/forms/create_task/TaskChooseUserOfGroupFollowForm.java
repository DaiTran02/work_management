package com.ngn.tdnv.doc.forms.create_task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.models.model_of_organization.GroupOranizationExModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class TaskChooseUserOfGroupFollowForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private TextField txtSearch = new TextField();
	private ButtonTemplate btnConfirm = new ButtonTemplate("Xác nhận");
	private ButtonTemplate btnClose = new ButtonTemplate("Đóng");

	private Grid<ApiUserGroupExpandModel> grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);
	private List<ApiUserGroupExpandModel> listModel = new ArrayList<ApiUserGroupExpandModel>();
	private Span spNameGroup = new Span();
	private Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapUserIsChoose = new HashMap<GroupOranizationExModel, ApiUserGroupExpandModel>();
	private GroupOranizationExModel groupKey;
	private List<Checkbox> listCheckBox = new ArrayList<Checkbox>();
	private Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapUserSelected;

	private String orgId;
	public TaskChooseUserOfGroupFollowForm(String orgId) {
		this.orgId = orgId;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		Span spTitle = new Span("*Ghi chú: Nhiệm vụ sẽ được theo dõi bởi người dùng được chọn");
		spNameGroup.getStyle().setFontWeight(600);
		this.add(spTitle,createToolbar(),spNameGroup,createGrid());
	}

	@Override
	public void configComponent() {
		btnConfirm.addClickListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
	}


	public void loadData(GroupOranizationExModel groupOranizationExModel,Map<GroupOranizationExModel, ApiUserGroupExpandModel> userSelected) {
		spNameGroup.setText("*Khối theo dõi: "+groupOranizationExModel.getName());
		groupKey = groupOranizationExModel;
		listModel = new ArrayList<ApiUserGroupExpandModel>();
		ApiResultResponse<List<ApiUserGroupExpandModel>> listUser = ApiOrganizationService.getListUserGroup(orgId, groupOranizationExModel.getGroupId());
		if(listUser.isSuccess()) {
			listModel.addAll(listUser.getResult());
		}
		
		this.mapUserSelected = userSelected;
		
		checkToConfirm();
		grid.setItems(listModel);
	}

	private Component createToolbar() {
		HorizontalLayout hLayoutToolbar = new HorizontalLayout();

		txtSearch.setWidthFull();

		btnConfirm.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		btnClose.addThemeVariants(ButtonVariant.LUMO_ERROR);

		hLayoutToolbar.setWidthFull();
		hLayoutToolbar.add(txtSearch,btnConfirm,btnClose);

		return hLayoutToolbar;
	}

	private Component createGrid() {
		grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);

		grid.addComponentColumn(model->{
			Checkbox cbChoose = new Checkbox();

			cbChoose.addClickListener(e->{
				if(mapUserIsChoose.containsKey(groupKey)) {
					mapUserIsChoose.remove(groupKey);
				}

				if(cbChoose.getValue()) {
					mapUserIsChoose.putIfAbsent(groupKey, model);
				}else {
					mapUserIsChoose.remove(groupKey, model);
				}
				
				listCheckBox.stream().forEach(cb->{
					if(!cb.equals(e.getSource())) {
						cb.setValue(false);
					}
				});
				checkToConfirm();
			});
			
			if(mapUserSelected != null && mapUserSelected.containsKey(groupKey)) {
				if(mapUserSelected.get(groupKey) != null && mapUserSelected.get(groupKey).equals(model)) {
					cbChoose.setValue(true);
					mapUserIsChoose.put(groupKey, model);
				}
			}

			listCheckBox.add(cbChoose);

			return cbChoose;
		}).setWidth("50px").setFlexGrow(0);

		grid.addColumn(model->{
			return model.getMoreInfo().getFullName();
		}).setHeader("Tên");
		grid.addColumn(ApiUserGroupExpandModel::getPositionName).setHeader("Chức vụ");

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

	public Map<GroupOranizationExModel, ApiUserGroupExpandModel> getMapUserIsChoose() {
		return mapUserIsChoose;
	}
}

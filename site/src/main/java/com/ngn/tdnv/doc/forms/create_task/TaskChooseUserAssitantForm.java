package com.ngn.tdnv.doc.forms.create_task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.organization.ApiGroupExpandModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.models.model_of_organization.GroupOranizationExModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class TaskChooseUserAssitantForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout vLayout = new VerticalLayout();
	private TextField txtSearch = new TextField();
	private ButtonTemplate btnSearch = new ButtonTemplate(FontAwesome.Solid.SEARCH.create());
	private Map<GroupOranizationExModel, List<ApiUserGroupExpandModel>> mapUserOfGroup = new HashMap<GroupOranizationExModel, List<ApiUserGroupExpandModel>>();
	private Map<GroupOranizationExModel, List<Checkbox>> mapGroupAndCheckbox = new HashMap<GroupOranizationExModel, List<Checkbox>>();
	private Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapGroupAndUserIsChoose = new HashMap<GroupOranizationExModel, ApiUserGroupExpandModel>();

	private String orgId;
	private Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapAssistantIsDrafting;
	public TaskChooseUserAssitantForm(String orgId,Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapAssistantIsDrafting) {
		this.orgId = orgId;
		this.mapAssistantIsDrafting = mapAssistantIsDrafting;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		Span spTitle = new Span("*Trong mỗi tổ giao việc sẽ có danh sách người sẽ giao việc trong mỗi tổ đó, hãy chọn người sẽ thực hiện giao nhiệm vụ (mặc định sẽ là người đang soạn nhiệm vụ).");
		this.add(spTitle,createToolbar(),vLayout);
	}

	@Override
	public void configComponent() {
		txtSearch.addValueChangeListener(e->loadData());
		btnSearch.addClickListener(e->loadData());
		btnSearch.addClickShortcut(Key.ENTER);
	}

	private void loadData() {
		ApiResultResponse<List<ApiGroupExpandModel>> listApiGroups = ApiOrganizationService.getListGroup(orgId,txtSearch.getValue());
		mapUserOfGroup.clear();
		if(listApiGroups.isSuccess()) {
			listApiGroups.getResult().stream().forEach(model->{
				GroupOranizationExModel groupOranizationExModel = new GroupOranizationExModel(model);
				mapUserOfGroup.putIfAbsent(groupOranizationExModel, getListUserOfGroup(model.getGroupId()));
			});
		}

		GroupOranizationExModel groupOranizationExModel = new GroupOranizationExModel();
		groupOranizationExModel.setGroupId(orgId);
		groupOranizationExModel.setName("Ngoài tổ");

		mapUserOfGroup.putIfAbsent(groupOranizationExModel, getListUserNotInGroup());

		vLayout.removeAll();
		createLayout(mapUserOfGroup);
	}

	private List<ApiUserGroupExpandModel> getListUserOfGroup(String idGroup){
		ApiResultResponse<List<ApiUserGroupExpandModel>> listUser = ApiOrganizationService.getListUserGroup(orgId, idGroup);
		if(listUser.isSuccess()) {
			return listUser.getResult();
		}
		return Collections.emptyList();
	}

	private List<ApiUserGroupExpandModel> getListUserNotInGroup(){
		ApiResultResponse<List<ApiUserGroupExpandModel>> listUserNotInOrg = ApiOrganizationService.getListUserNotInGroup(orgId);
		if(listUserNotInOrg.isSuccess()) {
			return listUserNotInOrg.getResult();
		}
		return Collections.emptyList();
	}

	private void createLayout(Map<GroupOranizationExModel, List<ApiUserGroupExpandModel>> data) {
		data.forEach((k,v)->{
			VerticalLayout vLayoutItem = new VerticalLayout();
			H5 header = new H5(k.getName());
			Grid<ApiUserGroupExpandModel> grid = new Grid<ApiUserGroupExpandModel>(ApiUserGroupExpandModel.class,false);
			grid.setAllRowsVisible(true);
			grid.setVisible(false);
			if(!v.isEmpty()) {
				grid.setVisible(true);
				grid.setItems(v);
				grid.addComponentColumn(model->{
					Checkbox cbChooseUser = new Checkbox();

					mapAssistantIsDrafting.forEach((ku,vu)->{
						if(ku.getGroupId().equals(k.getGroupId())) {
							if(vu.getUserId().equals(model.getUserId())) {
								cbChooseUser.setValue(true);
							}
						}
					});

					cbChooseUser.addClickListener(e->{
						mapGroupAndCheckbox.forEach((org,cb)->{
							cb.forEach(cbModel->{
								if(cbModel.equals(e.getSource())) {

								}else {
									cbModel.setValue(false);
								}
							});
						});
						mapGroupAndUserIsChoose.clear();
						mapGroupAndUserIsChoose.put(k, model);
						
						fireEvent(new ClickEvent(this, false));
						
					});

					mapGroupAndCheckbox.computeIfAbsent(k, m->new ArrayList<>()).add(cbChooseUser);

					return cbChooseUser;
				}).setWidth("60px").setFlexGrow(0);
				grid.addColumn(model->{
					return model.getMoreInfo().getFullName();
				});
				String height = v.isEmpty() ? "0px" : "150px";
				grid.setHeight(height);
				grid.getStyle().setPadding("0");
				grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

			}

			Span spNah = new Span("Không có người dùng nào");
			spNah.setVisible(v.isEmpty() ? true : false);

			vLayoutItem.add(header,new Hr(),grid,spNah);
			vLayoutItem.getStyle().setBoxShadow("rgba(0, 0, 0, 0.05) 0px 6px 24px 0px, rgba(0, 0, 0, 0.08) 0px 0px 0px 1px").setPadding("10px");
			vLayoutItem.setHeight("auto");

			vLayout.add(vLayoutItem);
		});
	}

	public void save() {
		fireEvent(new ClickEvent(this,false));
	}

	private Component createToolbar() {
		HorizontalLayout hLayoutToolbar = new HorizontalLayout();

		txtSearch.setWidthFull();

		hLayoutToolbar.add(txtSearch,btnSearch);
		hLayoutToolbar.setWidthFull();

		return hLayoutToolbar;
	}

	public Map<GroupOranizationExModel, ApiUserGroupExpandModel> getMapGroupAndUserIsChoose() {
		return mapGroupAndUserIsChoose;
	}

}

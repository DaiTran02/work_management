package vn.com.ngn.page.setting.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiPermissionModel;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.PermissionModel;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;

public class PermissionTemplateForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout vLayout = new VerticalLayout();
	private List<PermissionModel> listModel = new ArrayList<PermissionModel>();
	private List<PermissionModel> listPermissionSelected = new ArrayList<PermissionModel>();
	private Checkbox cbChooseAll = new Checkbox("Chọn tất cả");

	private Map<String, List<PermissionModel>> mapDataPermission = new HashMap<String, List<PermissionModel>>();
	private Map<String, List<PermissionModel>> mapPermissionIsChoose = new HashMap<String, List<PermissionModel>>();

	RoleOrganizationExpandsModel roleOrganizationExpandsModel = new RoleOrganizationExpandsModel();

	private String roleId;
	private Runnable onRun;
	public PermissionTemplateForm(String roleId,Runnable onRun) {
		this.onRun = onRun;
		this.roleId = roleId;
		buildLayout();
		
		loadData();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		cbChooseAll.getStyle().set("margin-left", "26px");
		this.add(cbChooseAll,vLayout);
		vLayout.setSizeFull();
	}

	@Override
	public void configComponent() {
		cbChooseAll.addClickListener(e->{
			if(cbChooseAll.getValue()) {
				mapPermissionIsChoose.clear();
				mapPermissionIsChoose.putAll(mapDataPermission);
				vLayout.removeAll();
				createGroupGrid();
			}else {
				loadData();
			}
		});
	}

	public void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiPermissionModel>> getListPermission = ApiOrganizationService.getListPermision();
			listModel = getListPermission.getResult().stream().map(PermissionModel::new).collect(Collectors.toList());
			mapDataPermission.clear();
			mapPermissionIsChoose.clear();
			listModel.forEach(model->{
				mapDataPermission.computeIfAbsent(model.getGroupId(),  m->new ArrayList<>()).add(model);
				mapPermissionIsChoose.computeIfAbsent(model.getGroupId(), l->new ArrayList<>());
			});
			vLayout.removeAll();
			loadDataOldRole();
			createGroupGrid();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadDataOldRole() {
		listPermissionSelected.clear();

		try {
			ApiResultResponse<ApiRoleOrganizationExpandsModel> getARole = ApiOrganizationService.getOneRoleTemplate(roleId);
			RoleOrganizationExpandsModel roleOrganizationExpandsModell = new RoleOrganizationExpandsModel(getARole.getResult());
			for(String permiss : roleOrganizationExpandsModell.getPermissionKeys()) {
				listModel.forEach(model->{
					if(permiss.equals(model.getKey())) {
						listPermissionSelected.add(model);
					}
				});
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		for(PermissionModel permissionModel : listPermissionSelected) {
			if(mapPermissionIsChoose.containsKey(permissionModel.getGroupId())){
				mapPermissionIsChoose.get(permissionModel.getGroupId()).add(permissionModel);
			}
		}
	}


	private void createGroupGrid() {
		mapDataPermission.forEach((k,v)->{
			VerticalLayout vLayoutItem = new VerticalLayout();
			vLayoutItem.getStyle().setBorder("1px solid #efefef");

			H5 header = new H5(v.get(0).getGroupName());
			Grid<PermissionModel> gridItem = new Grid<PermissionModel>(PermissionModel.class,false);
			vLayoutItem.add(header,gridItem);

			gridItem.setItems(v);
			gridItem.setAllRowsVisible(true);
			gridItem.setSelectionMode(Grid.SelectionMode.MULTI);
			gridItem.addColumn(PermissionModel::getName).setHeader("Tên");
			gridItem.addColumn(PermissionModel::getDescription).setHeader("Mô tả");
			gridItem.addSelectionListener(e->{
				listPermissionSelected.clear();
				listPermissionSelected.addAll(e.getAllSelectedItems());
				if(mapPermissionIsChoose.containsKey(k)) {
					mapPermissionIsChoose.remove(k);
					mapPermissionIsChoose.computeIfAbsent(k, l->new ArrayList<>()).addAll(e.getAllSelectedItems());
				}
			});

			gridItem.addThemeVariants(GridVariant.LUMO_COMPACT);
			gridItem.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
			gridItem.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
			gridItem.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
			gridItem.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

			mapPermissionIsChoose.get(k).forEach(model->{
				gridItem.select(model);
			});

			vLayout.add(vLayoutItem);
		});
	}

	public void doPermission() {
		listPermissionSelected.clear();
		Set<PermissionModel> listDataToUpdate = new LinkedHashSet<PermissionModel>();
		mapPermissionIsChoose.forEach((k,v)->{
			listDataToUpdate.addAll(mapPermissionIsChoose.get(k));
		});
		try {
			ApiResultResponse<ApiRoleOrganizationExpandsModel> getARole = ApiOrganizationService.getOneRoleTemplate(roleId);
			roleOrganizationExpandsModel = new RoleOrganizationExpandsModel(getARole.getResult());
			roleOrganizationExpandsModel.getPermissionKeys().clear();
			for(PermissionModel permissionModel : listDataToUpdate) {
				roleOrganizationExpandsModel.getPermissionKeys().add(permissionModel.getKey());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		doUpdate(roleOrganizationExpandsModel);
	}

	private void doUpdate(RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
		try {
			ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel = new ApiRoleOrganizationExpandsModel(roleOrganizationExpandsModel);
			ApiResultResponse<Object> updateRole = ApiOrganizationService.updateRoleTemplate(roleId, apiRoleOrganizationExpandsModel);
			if(updateRole.getStatus() == 200) {
				onRun.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}

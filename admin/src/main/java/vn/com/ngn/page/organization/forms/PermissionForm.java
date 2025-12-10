package vn.com.ngn.page.organization.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiPermissionModel;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.PermissionModel;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;

public class PermissionForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private Grid<PermissionModel> grid = new Grid<PermissionModel>(PermissionModel.class,false);
	private List<PermissionModel> listModel = new ArrayList<PermissionModel>();
	private List<PermissionModel> listPermissionSelected = new ArrayList<PermissionModel>();
	
	RoleOrganizationExpandsModel roleOrganizationExpandsModel = new RoleOrganizationExpandsModel();
	
	private String parentId,roleId;
	private Runnable onRun;
	private boolean checkAuthority = false;
	public PermissionForm(String parentId,String roleId,boolean checkAuthority,Runnable onRun) {
		this.onRun = onRun;
		this.parentId = parentId;
		this.roleId = roleId;
		this.checkAuthority = checkAuthority;
		buildLayout();
		loadData();
		loadDataOldRole();
		configComponent();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createGrid());
	}

	@Override
	public void configComponent() {
	
	}
	
	public void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiPermissionModel>> getListPermission = ApiOrganizationService.getListPermision();
			listModel = getListPermission.getResult().stream().map(PermissionModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		grid.setItems(listModel);
	}
	
	private void loadDataOldRole() {
		listPermissionSelected.clear();
		
		try {
			ApiResultResponse<ApiRoleOrganizationExpandsModel> getARole = ApiOrganizationService.getOneRole(parentId, roleId);
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
		if(!checkAuthority) {
			for(PermissionModel permissionModel : listPermissionSelected) {
				grid.select(permissionModel);
			}
		}
	}
	
	private Component createGrid() {
		grid = new Grid<PermissionModel>(PermissionModel.class,false);
		
		
		if(checkAuthority) {
			grid.setSelectionMode(SelectionMode.NONE);
			grid.addComponentColumn(model->{
				Checkbox cbChoose = new Checkbox();
				
				boolean check = listPermissionSelected.stream().filter(ft->ft.getId().equals(model.getId())).findFirst().isPresent();
				if(check) {
					cbChoose.setValue(true);
					cbChoose.setReadOnly(true);
				}else {
					cbChoose.setValue(false);
					cbChoose.setReadOnly(true);
				}
				
				return cbChoose;
			}).setWidth("45px").setFlexGrow(0);
		}else {
			grid.setSelectionMode(Grid.SelectionMode.MULTI);
			grid.addSelectionListener(e->{
				listPermissionSelected.clear();
				listPermissionSelected.addAll(e.getAllSelectedItems());
			});
		}
		
		grid.addColumn(PermissionModel::getName).setHeader("Tên").setResizable(true);
		grid.addColumn(PermissionModel::getDescription).setHeader("Mô tả");
		
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		return grid;
	}
	
	public void doPermission() {
		try {
			ApiResultResponse<ApiRoleOrganizationExpandsModel> getARole = ApiOrganizationService.getOneRole(parentId, roleId);
			roleOrganizationExpandsModel = new RoleOrganizationExpandsModel(getARole.getResult());
			roleOrganizationExpandsModel.getPermissionKeys().clear();
			for(PermissionModel permissionModel : listPermissionSelected) {
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
			ApiResultResponse<Object> updateRole = ApiOrganizationService.updateRole(parentId, roleId, apiRoleOrganizationExpandsModel);
			if(updateRole.getStatus() == 200) {
				onRun.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

}

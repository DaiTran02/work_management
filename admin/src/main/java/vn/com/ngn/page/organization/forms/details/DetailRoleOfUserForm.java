package vn.com.ngn.page.organization.forms.details;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiPermissionModel;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.forms.RoleOrgExpandsForm;
import vn.com.ngn.page.organization.models.PermissionModel;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.TabsTemplate;

public class DetailRoleOfUserForm extends TabsTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout vLayoutRole = new VerticalLayout();
	private ButtonTemplate btnAddMoreRole = new ButtonTemplate("Cập nhật vai trò",FontAwesome.Solid.PLUS.create());

	private List<RoleOrganizationExpandsModel> listModels = new ArrayList<RoleOrganizationExpandsModel>();
	private Grid<RoleOrganizationExpandsModel> gridRole = new Grid<RoleOrganizationExpandsModel>(RoleOrganizationExpandsModel.class,false);

	private List<PermissionModel> listPermissions = new ArrayList<PermissionModel>();
	private Grid<PermissionModel> gridPermisson = new Grid<PermissionModel>(PermissionModel.class,false);
	private Set<PermissionModel> mapPermiss = new LinkedHashSet<PermissionModel>();
	private List<PermissionModel> listPermissDuplicate = new ArrayList<PermissionModel>();

	private List<RoleOrganizationExpandsModel> listRoleOfUsers;
	private String idOrg;
	private String idUser;
	public DetailRoleOfUserForm(String idOrg,String idUser,List<RoleOrganizationExpandsModel> listRoleOfUsers) {
		if(listRoleOfUsers != null) {
			this.listRoleOfUsers = listRoleOfUsers;
		}
		this.idOrg = idOrg;
		this.idUser = idUser;
		buildLayout();
		configComponent();
		loadPermission();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		createLayout();

	}

	@Override
	public void configComponent() {
		btnAddMoreRole.addClickListener(e->{
			openDialogAttachRoleForUser();
		});
	}

	private void createLayout() {


		btnAddMoreRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAddMoreRole.getStyle().set("margin-left", "auto");

		vLayoutRole.add(btnAddMoreRole,createLayoutRole());



		addTab(new Span("Vai trò"), vLayoutRole);
		addTab(new Span("Quyền hạn"), createLayoutPermission());


		tabSheet.addSelectedChangeListener(e->{
			if(tabSheet.getSelectedIndex() == 2) {
				loadPermission();
			}
		});


	}

	private void loadData() {
		listModels = new ArrayList<RoleOrganizationExpandsModel>();
		listRoleOfUsers = new ArrayList<RoleOrganizationExpandsModel>();
		try {
			if(idOrg != null) {
				ApiResultResponse<List<ApiRoleOrganizationExpandsModel>> getListRule = ApiOrganizationService.getListRole(idOrg);
				listModels = getListRule.getResult().stream().map(RoleOrganizationExpandsModel::new).collect(Collectors.toList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		listModels.stream().filter(role->role.getUserIds().contains(idUser))
		.forEach(listRoleOfUsers::add);
		gridRole.setItems(listRoleOfUsers);

		loadPermission();
		fireEvent(new ClickEvent(this,false));
	}

	private Component createLayoutRole() {
		gridRole = new Grid<RoleOrganizationExpandsModel>(RoleOrganizationExpandsModel.class,false);
		gridRole.addColumn(RoleOrganizationExpandsModel::getName).setHeader("Tên vai trò").setWidth("250px").setFlexGrow(0).setResizable(true);
//		gridRole.addColumn(RoleOrganizationExpandsModel::getDescription).setHeader("Mô tả");
		gridRole.addComponentColumn(model->{
			VerticalLayout hLayout = new VerticalLayout();
			hLayout.setWidthFull();
			
			List<PermissionModel> listPermissionModels = getDataPermission(model.getPermissionKeys());
			
			Span spDesr = new Span(model.getDescription());
			
			
			Span spPermission = new Span("Quyền hạn");
			spPermission.getStyle().set("font-weight", "600");
			
			hLayout.add(spDesr,spPermission);
			
			listPermissionModels.stream().forEach(permission->{
				Span spPermiss = new Span("+"+permission.getName());
				
				spPermiss.getStyle().set("margin-left", "10px");
				
				hLayout.add(spPermiss);
			});
			
			return hLayout;
		}).setHeader("Mô tả");
		gridRole.addComponentColumn(model->{
			ButtonTemplate btnRemove = new ButtonTemplate(FontAwesome.Solid.REMOVE.create());
			btnRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnRemove.setTooltipText("Xóa vai trò");
			btnRemove.addClickListener(e->{
				openConfirmRemoveUserInRole(model.getRoleId(), model);
			});

			return btnRemove;
		}).setWidth("60px").setFlexGrow(0);
		gridRole.setItems(listRoleOfUsers);
		gridRole.setAllRowsVisible(true);
		gridRole.addThemeVariants(GridVariant.LUMO_COMPACT);
		gridRole.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		gridRole.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		gridRole.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		gridRole.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		return gridRole;
	}

	private void openDialogAttachRoleForUser() {
		DialogTemplate dialogTemplate = new DialogTemplate("Danh sách vai trò",()->{});

		RoleOrgExpandsForm roleOrgExpandsForm = new RoleOrgExpandsForm(idOrg,idUser);
		dialogTemplate.add(roleOrgExpandsForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.getBtnSave().setText("Cập nhật");
		dialogTemplate.getBtnSave().addClickListener(e->{
			roleOrgExpandsForm.acttachRoleForUser();
			fireEvent(new ClickEvent(this,false));
		});
		roleOrgExpandsForm.addChangeListener(e->{
			loadData();
			dialogTemplate.close();
			fireEvent(new ClickEvent(this,false));
		});
		dialogTemplate.open();
	}

	private void openConfirmRemoveUserInRole(String idRole,RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Bỏ gán vai trò");
		confirmDialogTemplate.setText("Xác nhận bỏ gán vai trò cho người dùng này");
		confirmDialogTemplate.addConfirmListener(e->updateRoles(idRole, roleOrganizationExpandsModel));
		confirmDialogTemplate.setCancelable(true);
		confirmDialogTemplate.open();
	}

	private void updateRoles(String idRole,RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
		roleOrganizationExpandsModel.getUserIds().removeIf(id -> id.equals(idUser));
		doUpdate(idRole, roleOrganizationExpandsModel);
	}

	private void doUpdate(String roleId,RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
		try {
			ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel = new ApiRoleOrganizationExpandsModel(roleOrganizationExpandsModel);
			ApiResultResponse<Object> updateRole = ApiOrganizationService.updateRole(idOrg, roleId, apiRoleOrganizationExpandsModel);
			if(updateRole.getStatus() == 200) {
				loadData();
				NotificationTemplate.success("Bỏ gán vai trò thành công");
				fireEvent(new ClickEvent(this,false));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Layout Permission
	
	
	private List<PermissionModel> getDataPermission(List<String> listkey){
		List<PermissionModel> listData = new ArrayList<PermissionModel>();
		listPermissions = new ArrayList<PermissionModel>();
		try {
			ApiResultResponse<List<ApiPermissionModel>> getListPermission = ApiOrganizationService.getListPermision();
			listPermissions = getListPermission.getResult().stream().map(PermissionModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		listkey.stream().forEach(model->{
			listPermissions.stream().filter(permiss->permiss.getKey().equals(model)).forEach(listData::add);
		});
		
		return listData;
		
	}
	
	private void loadPermission() {
		listPermissions = new ArrayList<PermissionModel>();
		try {
			ApiResultResponse<List<ApiPermissionModel>> getListPermission = ApiOrganizationService.getListPermision();
			listPermissions = getListPermission.getResult().stream().map(PermissionModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<PermissionModel> listData = new ArrayList<PermissionModel>();

		listRoleOfUsers.forEach(model->{
			model.getPermissionKeys().stream().forEach(modelPer->{
				listPermissions.stream().filter(permiss->permiss.getKey().equals(modelPer)).forEach(listData::add);
			});
			//			listPermissions.stream().filter(permiss->permiss.getKey().equals(model.getPermissionKeys())).forEach(listData::add);
		});

		mapPermiss = new LinkedHashSet<PermissionModel>(listData);
		listPermissDuplicate = new ArrayList<PermissionModel>(mapPermiss);
		gridPermisson.setItems(listPermissDuplicate);
	}

	private Component createLayoutPermission() {
		gridPermisson = new Grid<PermissionModel>(PermissionModel.class,false);

		gridPermisson.addColumn(PermissionModel::getName).setHeader("Tên quyền").setWidth("250px").setFlexGrow(0).setResizable(true);
		gridPermisson.addColumn(PermissionModel::getDescription).setHeader("Mô tả");

		gridPermisson.setItems(listPermissDuplicate);
		gridPermisson.setAllRowsVisible(true);
		gridPermisson.addThemeVariants(GridVariant.LUMO_COMPACT);
		gridPermisson.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		gridPermisson.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		gridPermisson.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		gridPermisson.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

		return gridPermisson;
	}

}

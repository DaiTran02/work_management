package vn.com.ngn.page.organization.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class RoleOrgExpandsForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Grid<RoleOrganizationExpandsModel> grid = new Grid<RoleOrganizationExpandsModel>(RoleOrganizationExpandsModel.class,false);
	private List<RoleOrganizationExpandsModel> listModel = new ArrayList<RoleOrganizationExpandsModel>();
	
	private TextField txtSearch = new TextField();
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());
	private Button btnAddRole = new Button("Thêm vai trò mới",FontAwesome.Solid.PLUS.create());
	private Button btnAddRoleFormTemplate = new Button("Thêm vai trò từ vai trò mẫu",FontAwesome.Solid.PLUS.create());
	
	private List<RoleOrganizationExpandsModel> listRoleIsUserChoose = new ArrayList<RoleOrganizationExpandsModel>();
	private List<RoleOrganizationExpandsModel> listRoleIsSelect = new ArrayList<RoleOrganizationExpandsModel>();
	private List<RoleOrganizationExpandsModel> listRoleNotSelect = new ArrayList<RoleOrganizationExpandsModel>();

	private String parentId;
	private boolean checkRouterFromUser = false;
	private String idUser;
	public RoleOrgExpandsForm(String parentId,String idUser) {
		this.parentId = parentId;
		loadData();
		if(idUser != null) {
			this.idUser = idUser;
			checkRouterFromUser = true;
			loadDataRoleForUser();
		}
		buildLayout();
		configComponent();
		
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createToolbar(),createGrid());
	}

	@Override
	public void configComponent() {
		btnAddRole.addClickListener(e->{
			openDialogCreateRole();
		});
		
		btnAddRoleFormTemplate.addClickListener(e->{
			openDialogAddRoleTemplate();
		});
	}
	
	public void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiRoleOrganizationExpandsModel>> getListRule = ApiOrganizationService.getListRole(parentId);
			listModel = getListRule.getResult().stream().map(RoleOrganizationExpandsModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		grid.setItems(listModel);
		
	}
	
	private void loadDataRoleForUser() {
		listModel.stream()
			.filter(role->role.getUserIds().contains(idUser))
				.forEach(listRoleIsUserChoose::add);
	}
	
	private Component createGrid() {
		grid = new Grid<RoleOrganizationExpandsModel>(RoleOrganizationExpandsModel.class,false);
		
		if(checkRouterFromUser) {
			grid.addComponentColumn(model->{
				Checkbox checkbox = new Checkbox();
				
				if(!listRoleIsUserChoose.isEmpty()) {
					listRoleIsUserChoose.stream().forEach(modelRole->{
						if(modelRole.getRoleId().equals(model.getRoleId())) {
							checkbox.setValue(true);
							listRoleIsSelect.add(modelRole);
						}
					});
				}
				
				checkbox.addClickListener(e->{
					if(checkbox.getValue()) {
						listRoleIsSelect.add(model);
						listRoleNotSelect.remove(model);
					}else {
						listRoleIsSelect.remove(model);
						listRoleNotSelect.add(model);
					}
				});
				
				
				return checkbox;
			}).setWidth("50px").setFlexGrow(0);
		}
		
		grid.addColumn(RoleOrganizationExpandsModel::getName).setHeader("Tên vai trò");
		grid.addColumn(RoleOrganizationExpandsModel::getDescription).setHeader("Mô tả vai trò");
		grid.addComponentColumn(model->{
			if(model.getRoleTemplateId() != null) {
				return createStatusIcon("Available");
			}else {
				return createStatusIcon("NotAvailable");
			}
		}).setHeader("Trạng thái").setWidth("110px").setFlexGrow(0);
		
		grid.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();
			boolean checkAuthority = model.getRoleTemplateId() == null ? false : true;
			Button btnPermisson = new Button("Phân quyền ("+model.getPermissionKeys().size()+")");
			btnPermisson.getStyle().setCursor("pointer");
			btnPermisson.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnPermisson.setWidth("120px");
			btnPermisson.addClickListener(e->{
				openDialogPermission(model.getName(), model.getRoleId(),checkAuthority,false);
			});
			
			Button btnUsers = new Button("Người dùng ("+model.getUserIds().size()+")");
			btnUsers.getStyle().setCursor("pointer");
			btnUsers.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnUsers.setWidth("150px");
			btnUsers.addClickListener(e->{
				openDialogControlUser(model.getName(), model.getRoleId());
			});
			
			Button btnEdit = new Button(FontAwesome.Solid.EDIT.create());
			btnEdit.getStyle().setCursor("pointer");
			btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEdit.addClickListener(e->{
				openDialogUpdateRole(model.getRoleId(),checkAuthority);
			});
			
			Button btnRemove = new Button(FontAwesome.Solid.TRASH.create());
			btnRemove.getStyle().setCursor("pointer");
			btnRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnRemove.addClickListener(e->{
				openConfirmDelete(model.getRoleId());
			});
			
			if(model.getUserIds().size() > 0) {
				btnRemove.setEnabled(false);
			}
			
			if(checkAuthority) {
				btnPermisson = new Button("Xem quyền ("+model.getPermissionKeys().size()+")");
				btnPermisson.getStyle().setCursor("pointer");
				btnPermisson.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				btnPermisson.setWidth("120px");
				btnPermisson.addClickListener(e->{
					openDialogPermission(model.getName(), model.getRoleId(),checkAuthority,true);
				});
			}
			
			hLayout.add(btnPermisson,btnUsers,btnEdit,btnRemove);
			return hLayout;
		}).setHeader("Thao tác");
		

		grid.setSizeFull();
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		grid.setItems(listModel);
		
		return grid;
	}
	
	private Component createToolbar() {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Nhập từ khóa để lọc");
		
		btnSearch.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSearch.getStyle().setCursor("pointer");
		
		btnAddRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAddRole.getStyle().setCursor("pointer");
		
		btnAddRoleFormTemplate.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAddRoleFormTemplate.getStyle().setCursor("pointer");
		
		hLayout.expand(txtSearch);
		hLayout.add(btnAddRole,btnAddRoleFormTemplate);
		hLayout.setWidthFull();
		
		return hLayout;
	}
	
	public void acttachRoleForUser() {
		listRoleIsSelect.removeIf(role-> listRoleIsUserChoose.contains(role));
		listRoleIsSelect.stream().forEach(model->{
			model.getUserIds().add(idUser);
			doUpdateRole(model.getRoleId(), model);
		});
		
		if(!listRoleNotSelect.isEmpty()) {
			listRoleNotSelect.stream().forEach(model->{
				model.getUserIds().remove(idUser);
				doUpdateRole(model.getRoleId(), model);
			});
		}
		fireEvent(new ClickEvent(this,false));
	}
	
	private void doUpdateRole(String roleId,RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
		try {
			ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel = new ApiRoleOrganizationExpandsModel(roleOrganizationExpandsModel);
			ApiResultResponse<Object> updateRole = ApiOrganizationService.updateRole(parentId, roleId, apiRoleOrganizationExpandsModel);
			if(updateRole.getStatus() == 200) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openDialogCreateRole() {
		DialogTemplate dialog = new DialogTemplate("THÊM VAI TRÒ MỚI",()->{
			loadData();
		});
		
		EditRoleOrgExpandsForm editRoleOrgExpandsForm = new EditRoleOrgExpandsForm(parentId, null,false, ()->{
			NotificationTemplate.success("Thêm vai trò thành công");
			loadData();
			dialog.close();
		});
		
		dialog.add(editRoleOrgExpandsForm);
		dialog.getBtnSave().addClickListener(e->{
			editRoleOrgExpandsForm.saveRole();
		});
		dialog.setWidth("70%");
		dialog.open();
	}
	
	private void openDialogUpdateRole(String roleId,boolean checkAuthority) {
		DialogTemplate dialog = new DialogTemplate("CHỈNH SỬA VAI TRÒ",()->{
			loadData();
		});
		
		EditRoleOrgExpandsForm editRoleOrgExpandsForm = new EditRoleOrgExpandsForm(parentId, roleId,checkAuthority, ()->{
			NotificationTemplate.success("Chỉnh sửa vai trò thành công");
			loadData();
			dialog.close();
		});
		
		dialog.add(editRoleOrgExpandsForm);
		dialog.getBtnSave().addClickListener(e->{
			editRoleOrgExpandsForm.saveRole();
		});
		dialog.setWidth("70%");
		dialog.open();
	}
	
	private void openDialogAddRoleTemplate() {
		DialogTemplate dialog = new DialogTemplate("THÊM VAI TRÒ TỪ VAI TRÒ MẪU",()->{
			
		});
		
		RoleTemplateForm roleTemplateForm = new RoleTemplateForm(parentId,null);
		
		dialog.add(roleTemplateForm);
		dialog.getBtnSave().addClickListener(e->{
			roleTemplateForm.createRole();
			NotificationTemplate.success("Thành công");
			loadData();
			dialog.close();
		});
		dialog.setWidth("90%");
		dialog.setHeight("90%");
		dialog.open();
	}
	
	private void openDialogPermission(String name,String roleId,boolean checkAuthority,boolean checkGlobal) {
		DialogTemplate dialog = new DialogTemplate("PHÂN QUYỀN - "+name,()->{
			loadData();
		});
		
		
		PermissionForm permisisonForm = new PermissionForm(parentId,roleId,checkAuthority,()->{
			loadData();
			NotificationTemplate.success("Phân quyền thành công");
			dialog.close();
		});
		
		dialog.add(permisisonForm);
		
		if(checkGlobal) {
			dialog.getFooter().removeAll();
		}else {
			dialog.getBtnSave().addClickListener(e->{
				permisisonForm.doPermission();
			});
		}
		
		
		dialog.setWidth("70%");
		dialog.open();
	}
	
	private void openDialogControlUser(String name,String roleId) {
		DialogTemplate dialog = new DialogTemplate("PHÂN NGƯỜI DÙNG CHO - "+name,()->{
			loadData();
		});
		
		TabDevisionUserForRoleForm tabDevisionUserForRoleForm = new TabDevisionUserForRoleForm(parentId, roleId);
		
		dialog.add(tabDevisionUserForRoleForm);
		
		dialog.getBtnSave().addClickListener(e->{
			
		});
		
		dialog.getFooter().removeAll(); 
		dialog.setWidth("100%");
		dialog.setHeight("99%");
		dialog.open();
	}
	
	
	
	private void openConfirmDelete(String roleId) {
		ConfirmDialogTemplate confirm = new ConfirmDialogTemplate("Xóa vai trò");
		confirm.setText("Xác nhận xóa vai trò");
		confirm.addConfirmListener(e->{
			deleteRole(roleId);
		});
		confirm.setConfirmButtonTheme("error");
		confirm.setCancelable(true);
		confirm.open();
	}
	
	private void deleteRole(String roleId) {
		try {
			ApiResultResponse<Object> deleteRole = ApiOrganizationService.deleteRole(parentId, roleId);
			if(deleteRole.getStatus()==200) {
				NotificationTemplate.success("Xóa vai trò thành công");
				loadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Button createStatusIcon(String status) {
		boolean isAvailable = "Available".equals(status);
		Button btnStatus;
		if (isAvailable) {
			btnStatus = new Button("Tham chiếu");
			btnStatus.setTooltipText("Vai trò sẽ được cập nhật theo vai trò mẫu.");
			btnStatus.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		} else {
			btnStatus = new Button("Tự quản");
			btnStatus.setTooltipText("Vai trò tự quản lý, không tự cập nhật theo vai trò mẫu.");
			btnStatus.addThemeVariants(ButtonVariant.LUMO_ERROR);
		}
		btnStatus.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		return btnStatus;
	}

}





































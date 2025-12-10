package vn.com.ngn.page.user.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiPermissionModel;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.api.user.ApiUserService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.forms.details.DetailRoleOfUserForm;
import vn.com.ngn.page.organization.models.ListUserIDsModel;
import vn.com.ngn.page.organization.models.OrganizationModel;
import vn.com.ngn.page.organization.models.PermissionModel;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;
import vn.com.ngn.page.user.model.BelongOrganizationsModel;
import vn.com.ngn.page.user.model.UserModel;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class ListOrgInUserForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Grid<OrganizationModel> grid = new Grid<OrganizationModel>(OrganizationModel.class,false);
	private List<OrganizationModel> listModel = new ArrayList<OrganizationModel>();
	private List<PermissionModel> listPermissions = new ArrayList<PermissionModel>();
	private List<RoleOrganizationExpandsModel> listRoles = new ArrayList<RoleOrganizationExpandsModel>();
	
	private List<BelongOrganizationsModel> listOrg;
	
	private String idUser;
	public ListOrgInUserForm(String idUser,List<BelongOrganizationsModel> listOrg) {
		this.idUser = idUser;
		this.listOrg = listOrg;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		Span span = new Span("*Danh sách đơn vị là danh sách đơn vị mà người dùng đang làm việc tại đơn vị đó,"
				+ " không thể xóa tài khoản khi mà người dùng đang còn ở trong bất kỳ đơn vị nào");
		
		span.getStyle().set("font-weight", "600");
		
		this.add(span,createGrid());
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		listModel.clear();
		
		List<ApiOrganizationModel> listDataApiOrg = new ArrayList<ApiOrganizationModel>();
		try {
			for(BelongOrganizationsModel belongOrganizationsModel : listOrg) {
				ApiResultResponse<ApiOrganizationModel> listDataOrg = ApiOrganizationService.getOneOrg(belongOrganizationsModel.getOrganizationId());
				listDataApiOrg.add(listDataOrg.getResult());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(ApiOrganizationModel apiOrganizationModel : listDataApiOrg) {
			listModel.add(new OrganizationModel(apiOrganizationModel));
		}
		
		grid.setItems(listModel);
	}
	
	private void loadNewData() {
		listOrg = new ArrayList<BelongOrganizationsModel>();
		try {
			ApiResultResponse<ApiUserModel> data = ApiUserService.getaUser(idUser);
			if(data.isSuscces()) {
				UserModel userModel = new UserModel(data.getResult());
				listOrg.addAll(userModel.getBelongOrganizations());
				loadData();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private Grid<OrganizationModel> createGrid(){
		grid = new Grid<OrganizationModel>(OrganizationModel.class,false);
		
		grid.addColumn(OrganizationModel::getName).setHeader("Đơn vị");
		grid.addColumn(OrganizationModel::getDescription).setHeader("Chức vụ tại đơn vị");
		grid.addComponentColumn(model->{
			ButtonTemplate btnControlRole = new ButtonTemplate("Xem vai trò");
			btnControlRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnControlRole.setTooltipText("Xem những vai trò của người dùng trong đơn vị này");
			btnControlRole.addClickListener(e->{
				openDialogViewRole(model);
			});
			
			ButtonTemplate btnRemoveUser = new ButtonTemplate(FontAwesome.Solid.REMOVE.create());
			btnRemoveUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnRemoveUser.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnRemoveUser.setTooltipText("Đưa người dùng ra khỏi đơn vị này");
			
			btnRemoveUser.addClickListener(e->{
				ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Xác nhận bỏ gán đơn vị");
				confirmDialogTemplate.setText("Bỏ người dùng ra khỏi đơn vị này, để đưa người dùng quay lại đơn vị vui lòng vào trong mục quản lý đơn vị");
				confirmDialogTemplate.addConfirmListener(ev->{
					removeUsers(model.getId());
				});
				
				confirmDialogTemplate.setCancelable(true);
				confirmDialogTemplate.open();
			});
			
			HorizontalLayout hLayout = new HorizontalLayout();
			hLayout.add(btnControlRole,btnRemoveUser);
			
			return hLayout;
		}).setHeader("Thao tác").setWidth("150px").setFlexGrow(0);
		
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		
		return grid;
	}
	
	
	private void removeUsers(String orgId) {
		try {
			ListUserIDsModel listUserIDsModel = new ListUserIDsModel();
			listUserIDsModel.getUserIds().add(idUser);
			
			ApiResultResponse<Object> removeUser = ApiOrganizationService.removeUsersFormOrg(orgId, listUserIDsModel);
			if(removeUser.getStatus()==200) {
				NotificationTemplate.success("Bỏ gán đơn vị khỏi người dùng thành công");
				loadNewData();
				fireEvent(new ClickEvent(this, false));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openDialogViewRole(OrganizationModel organizationModel) {
		
		DialogTemplate dialogTemplate = new DialogTemplate("Vai trò của người dùng trong đơn vị",()->{});
		
		VerticalLayout vLayout = new VerticalLayout();
		
		VerticalLayout vLayoutOrg = new VerticalLayout();
		vLayoutOrg.setWidthFull();
		vLayoutOrg.add(createLayoutKeyValue("Mô tả:", organizationModel.getDescription(), null,null));
		
		loadRole(organizationModel.getId());
		List<RoleOrganizationExpandsModel> listRoleOfUsers = new ArrayList<RoleOrganizationExpandsModel>();
		listRoles.stream().filter(role->role.getUserIds().contains(idUser))
		.forEach(roleName->{
			listRoleOfUsers.add(roleName);
		});
		
		for(RoleOrganizationExpandsModel roleOrganizationExpandsModel : listRoleOfUsers) {
			vLayoutOrg.add(createLayoutKeyValue("Vai trò: ", roleOrganizationExpandsModel.getName(), null,roleOrganizationExpandsModel.getPermissionKeys()));
		}
		
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		ButtonTemplate btnRole = new ButtonTemplate("Cập nhật vai trò",FontAwesome.Solid.EDIT.create());
		btnRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		hLayoutButton.add(btnRole);
		
		btnRole.addClickListener(e->{
			loadRole(organizationModel.getId());
			List<RoleOrganizationExpandsModel> listRoleOfUser = new ArrayList<RoleOrganizationExpandsModel>();
			listRoles.stream().filter(role->role.getUserIds().contains(idUser))
			.forEach(roleName->{
				listRoleOfUser.add(roleName);
			});
			openDialogPermission(organizationModel.getId(), idUser, listRoleOfUser,()->{
				dialogTemplate.close();
			});
		});
		vLayout.add(vLayoutOrg,new Hr(),hLayoutButton);
		
		dialogTemplate.add(vLayout);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeight("70%");
	}
	
	private Component createLayoutKeyValue(String name,String value,String style,List<String> names) {
		HorizontalLayout hLayoutKeyValue = new HorizontalLayout();
		Span spanName = new Span(name);
		
		spanName.getStyle().set("font-weight", "600");
		spanName.setWidth("65px");
		
		Span spanValue = new Span(value);
		if(style != null) {
			spanValue.getStyle().setColor(style);
		}
		
		VerticalLayout vLayoutValue = new VerticalLayout();
		vLayoutValue.setPadding(false);
		vLayoutValue.add(spanValue);
		if(names != null) {
			List<PermissionModel> data = getDataPermission(names);
			data.stream().forEach(model->{
				Span spanNamex = new Span("+"+model.getName());
				spanNamex.getStyle().set("margin-left", "5px");
				vLayoutValue.add(spanNamex);
			});
		}
		
		hLayoutKeyValue.add(spanName,vLayoutValue);
		return hLayoutKeyValue;
	}
	
	
	private void openDialogPermission(String orgId,String idUser,List<RoleOrganizationExpandsModel> listRoleOfUser,Runnable run) {
		DialogTemplate dialog = new DialogTemplate("Danh sách vai trò của người dùng",()->{
			loadData();
		});
		
		DetailRoleOfUserForm detailRoleOfUserForm = new DetailRoleOfUserForm(orgId,idUser,listRoleOfUser);
		dialog.add(detailRoleOfUserForm);
		detailRoleOfUserForm.addChangeListener(e->{
			loadNewData();
			fireEvent(new ClickEvent(this,false));
			run.run();
		});
	
		dialog.getBtnSave().setVisible(false);
		dialog.setSizeFull();
		dialog.open();
	}
	
	private List<PermissionModel> getDataPermission(List<String> listkeys){
		List<PermissionModel> listData = new ArrayList<PermissionModel>();
		listPermissions = new ArrayList<PermissionModel>();
		try {
			ApiResultResponse<List<ApiPermissionModel>> getListPermission = ApiOrganizationService.getListPermision();
			listPermissions = getListPermission.getResult().stream().map(PermissionModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		listkeys.stream().forEach(model->{
			listPermissions.stream().filter(permiss->permiss.getKey().equals(model)).forEach(listData::add);
		});
		
		return listData;
	}
	
	private void loadRole(String orgId) {
		listRoles = new ArrayList<RoleOrganizationExpandsModel>();
		try {
			if(orgId != null) {
				ApiResultResponse<List<ApiRoleOrganizationExpandsModel>> getListRule = ApiOrganizationService.getListRole(orgId);
				listRoles = getListRule.getResult().stream().map(RoleOrganizationExpandsModel::new).collect(Collectors.toList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

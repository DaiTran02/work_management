package vn.com.ngn.page.user.form.details;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiPermissionModel;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.api.user.ApiUserService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.forms.details.DetailRoleOfUserForm;
import vn.com.ngn.page.organization.models.OrganizationModel;
import vn.com.ngn.page.organization.models.PermissionModel;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DetailsTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class InfoOfOrgForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout vLayout = new VerticalLayout();
	private List<OrganizationModel> listOrg = new ArrayList<OrganizationModel>();
	private List<String> listIdOrg = new ArrayList<String>();
	private List<PermissionModel> listPermissions = new ArrayList<PermissionModel>();
	private List<RoleOrganizationExpandsModel> listRoles = new ArrayList<RoleOrganizationExpandsModel>();
	
	private String idUser;
	public InfoOfOrgForm(String idUser) {
		this.idUser = idUser;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(vLayout);
	}

	@Override
	public void configComponent() {
		
	}
	
	private void loadData() {
		listIdOrg = new ArrayList<String>();
		try {
			ApiResultResponse<ApiUserModel> data = ApiUserService.getaUser(idUser);
			if(data.isSuscces()) {
				data.getResult().getBelongOrganizations().stream().forEach(model->{
					listIdOrg.add(model.getOrganizationId());
				});
			}
		} catch (Exception e) {
		}
		loadDataOrg();
	}
	
	private void loadDataOrg() {
		listOrg = new ArrayList<OrganizationModel>();
		vLayout.removeAll();
		for(String idOrg : listIdOrg) {
			try {
				ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(idOrg);
				if(data.isSuscces()) {
					listOrg.add(new OrganizationModel(data.getResult()));
				}
			} catch (Exception e) {
			}
		}
		
		listOrg.stream().forEach(model->{
			createLayout(model);
		});
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
	
	private void createLayout(OrganizationModel organizationModel) {
		DetailsTemplate detailsTemplate = new DetailsTemplate(organizationModel.getName());
		
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
			openDialogPermission(organizationModel.getId(), idUser, listRoleOfUser);
		});
		
		detailsTemplate.add(vLayoutOrg,new Hr(),hLayoutButton);
		detailsTemplate.setOpened(false);
		vLayout.add(detailsTemplate);
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
	
	private void openDialogPermission(String orgId,String idUser,List<RoleOrganizationExpandsModel> listRoleOfUser) {
		DialogTemplate dialog = new DialogTemplate("Danh sách vai trò của người dùng",()->{
			loadData();
		});
		
		DetailRoleOfUserForm detailRoleOfUserForm = new DetailRoleOfUserForm(orgId,idUser,listRoleOfUser);
		dialog.add(detailRoleOfUserForm);
		detailRoleOfUserForm.addChangeListener(e->{
			loadData();
			fireEvent(new ClickEvent(this,false));
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
}

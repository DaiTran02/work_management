package com.ngn.setting.step_setup_user;

import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.organization.ApiOrganizationServiceCustom;
import com.ngn.api.organization.ApiPermissionModel;
import com.ngn.api.organization.ApiRoleOfOrgModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ThirdStepChooseRoleForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private final ApiOrganizationServiceCustom apiOrganizationServiceCustom;
	
	private Grid<ApiRoleOfOrgModel> grid = new Grid<ApiRoleOfOrgModel>(ApiRoleOfOrgModel.class,false);
	private List<ApiRoleOfOrgModel> listModel = new ArrayList<ApiRoleOfOrgModel>();
	
	private List<ApiPermissionModel> listPermission = new ArrayList<ApiPermissionModel>();
	private List<Checkbox> listCheck = new ArrayList<Checkbox>();
	
	private String idRole = "";
	
	private boolean addRole = false;
	private String idOrg;
	public ThirdStepChooseRoleForm(String idOrg,boolean addRole,ApiOrganizationServiceCustom apiOrganizationServiceCustom) {
		this.apiOrganizationServiceCustom = apiOrganizationServiceCustom;
		this.addRole = addRole;
		this.idOrg = idOrg;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		Span span = new Span("*Mỗi tài khoản sẽ có một vai trò khác nhau ở trong đơn vị và các chức năng cũng sẽ thay đổi theo vai trò, vui lòng chọn đúng vai trò của mình.");
		span.getStyle().setFontWeight(600);
		span.setVisible(false);
		if(addRole)
			span.setVisible(true);
		this.add(span,createGrid());
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		listModel = new ArrayList<ApiRoleOfOrgModel>();
		ApiResultResponse<List<ApiRoleOfOrgModel>> data = apiOrganizationServiceCustom.getListRoles(idOrg);
		if(data.isSuccess()) {
			listModel.addAll(data.getResult());
		}
		
		listPermission = new ArrayList<ApiPermissionModel>();
		ApiResultResponse<List<ApiPermissionModel>> dataPermission = apiOrganizationServiceCustom.getPermissions();
		if(dataPermission.isSuccess()) {
			listPermission.addAll(dataPermission.getResult());
		}
		
		grid.setItems(listModel);
	}
	
	private Component createGrid() {
		grid = new Grid<ApiRoleOfOrgModel>(ApiRoleOfOrgModel.class,false);
		
		
		if(addRole) {
			grid.addComponentColumn(model->{
				Checkbox cb = new Checkbox();
				
				cb.addClickListener(e->{
					idRole = "";
					listCheck.forEach(check->{
						if(cb.equals(check)) {
						}else {
							check.setValue(false);
						}
					});
					
					if(e.getSource().getValue()) {
						idRole = model.getRoleId();
					}else {
						idRole = null;
					}
					
					fireEvent(new ClickEvent(this, false));
				});
				
				listCheck.add(cb);
				return cb;
			}).setWidth("50px").setFlexGrow(0);
		}
		
		grid.addComponentColumn(model->{
			VerticalLayout vLayout = new VerticalLayout();
			
			H5 header = new H5(model.getName());
			
			vLayout.add(header);
			
			VerticalLayout vContent = new VerticalLayout();
			for(String p : model.getPermissionKeys()) {
				Span span = new Span(" + "+checkNamePermission(p));
				vContent.add(span);
			}
			vLayout.add(vContent);
			
			return vLayout;
		}).setHeader("Tên vai trò");
		
		grid.addComponentColumn(model->{
			ButtonTemplate btnUsersOfRole = new ButtonTemplate(model.getUserIds().size()+" người",FontAwesome.Solid.USERS.create());
			
			btnUsersOfRole.addClickListener(e->{
				openDialogUsersOfGroup(model.getRoleId());
			});
			
			return btnUsersOfRole;
		}).setHeader("Số người sử dụng vai trò");
		
		return grid;
	}
	
	private String checkNamePermission(String keyPermission) {
		return listPermission.stream().filter(model->model.getKey().equals(keyPermission)).findFirst().get().getName().toString();
	}
	
	private void openDialogUsersOfGroup(String idRole) {
		DialogTemplate dialogTemplate = new DialogTemplate("DANH SÁCH NGƯỜI DÙNG SỬ DỤNG VAI TRÒ");
		
		InfoUsersOfRoleForm infoUserOfRole = new InfoUsersOfRoleForm(apiOrganizationServiceCustom, idOrg, idRole);
		dialogTemplate.add(infoUserOfRole);
		
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setWidth("70%");
		dialogTemplate.setHeight("90%");
		dialogTemplate.open();
	}
	
	
	public String getIdRole() {
		return idRole;
	}

	

}

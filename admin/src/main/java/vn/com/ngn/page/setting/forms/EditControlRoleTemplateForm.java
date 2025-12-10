package vn.com.ngn.page.setting.forms;

import java.util.Collections;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;

public class EditControlRoleTemplateForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean checkNotNull = false;
	
	private TextField txtName = new TextField("Tên vai trò");
	private TextField txtDesr = new TextField("Mô tả");
	
	private RoleOrganizationExpandsModel roleOrgModel;
	
	private String roleId;
	private Runnable onRun;
	public EditControlRoleTemplateForm(String roleId,Runnable onRun) {
		this.onRun = onRun;
		buildLayout();
		configComponent();
		if(roleId != null) {
			this.roleId = roleId;
			checkNotNull = true;
			loadData();
		}
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createLayout());
	}

	@Override
	public void configComponent() {
		
	}
	
	public void loadData() {
		try {
			ApiResultResponse<ApiRoleOrganizationExpandsModel> data = ApiOrganizationService.getOneRoleTemplate(roleId);
			roleOrgModel = new RoleOrganizationExpandsModel(data.getResult());
			
			txtName.setValue(roleOrgModel.getName());
			txtDesr.setValue(roleOrgModel.getDescription());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private Component createLayout() {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		txtName.setWidthFull();
		txtDesr.setWidthFull();
		
		hLayout.add(txtName,txtDesr);
		hLayout.setSizeFull();
		return hLayout;
	}
	
	public void saveRole() {
		
		if(invalid() == false) {
			return;
		}
		
		RoleOrganizationExpandsModel roleOrganizationExpandsModel = new RoleOrganizationExpandsModel();
		roleOrganizationExpandsModel.setName(txtName.getValue());
		roleOrganizationExpandsModel.setDescription(txtDesr.getValue());
		if(checkNotNull) {
			roleOrganizationExpandsModel.setPermissionKeys(roleOrgModel.getPermissionKeys());
			roleOrganizationExpandsModel.setUserIds(roleOrgModel.getUserIds());
			doUpdateRole(roleOrganizationExpandsModel);
		}else {
			roleOrganizationExpandsModel.setPermissionKeys(Collections.emptyList());
			roleOrganizationExpandsModel.setUserIds(Collections.emptyList());
			doCreateRole(roleOrganizationExpandsModel);
		}
		
	}
	
	private void doCreateRole(RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
		try {
			ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel = new ApiRoleOrganizationExpandsModel(roleOrganizationExpandsModel);
			ApiResultResponse<Object> createRole = ApiOrganizationService.createRoleTemplate(apiRoleOrganizationExpandsModel);
			if(createRole.getStatus() == 200 || createRole.getStatus() == 201) {
				onRun.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void doUpdateRole(RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
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
	
	private boolean invalid() {
		
		if(txtName.getValue().isEmpty()) {
			txtName.setErrorMessage("Không được để trống");
			txtName.setInvalid(true);
			txtName.focus();
			return false;
		}
		
		if(txtDesr.getValue().isEmpty()) {
			txtDesr.setErrorMessage("Vui lòng nhập mô tả");
			txtDesr.setInvalid(true);
			txtDesr.focus();
			return false;
		}
		
		return true;
	}

}

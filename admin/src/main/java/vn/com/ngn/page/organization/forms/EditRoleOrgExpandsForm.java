package vn.com.ngn.page.organization.forms;

import java.util.Collections;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;

public class EditRoleOrgExpandsForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean checkNotNull = false;

	private RoleOrganizationExpandsModel roleOrgModel;

	private TextField txtName = new TextField("Tên vai trò");
	private TextArea  txtDesr = new TextArea("Mô tả");
	private Checkbox cbAutoUpdate = new Checkbox("Ủy quyền (Nếu bỏ ủy quyền thì không thể gán ủy quyền lại lần nữa)");

	private String parentId;
	private String roleId;
	private Runnable onRun;
	private boolean checkAuthority = false;
	public EditRoleOrgExpandsForm(String parentId,String roleId,boolean checkAuthority ,Runnable onRun) {
		this.onRun = onRun;
		this.parentId = parentId;
		this.checkAuthority = checkAuthority;
		buildLayout();
		configComponent();
		if(roleId!=null) {
			checkNotNull = true;
			this.roleId = roleId;
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
			ApiResultResponse<ApiRoleOrganizationExpandsModel> getRole = ApiOrganizationService.getOneRole(parentId, roleId);
			roleOrgModel = new RoleOrganizationExpandsModel(getRole.getResult());
			txtName.setValue(roleOrgModel.getName());
			txtDesr.setValue(roleOrgModel.getDescription());
			if(roleId == null) {
				cbAutoUpdate.setVisible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Component createLayout() {
		VerticalLayout hLayout = new VerticalLayout();

		txtName.setWidthFull();
		txtDesr.setWidthFull();
		txtDesr.setHeight("100px");
		cbAutoUpdate.setVisible(false);
		
		if(checkAuthority) {
			cbAutoUpdate.setVisible(true);
			cbAutoUpdate.setValue(checkAuthority);
		}
		hLayout.add(txtName,txtDesr,cbAutoUpdate);
		hLayout.setSizeFull();
		return hLayout;
	}

	public void saveRole() {
		if(!invalid()) {
			return;
		}
		RoleOrganizationExpandsModel roleOrganizationExpandsModel = new RoleOrganizationExpandsModel();
		roleOrganizationExpandsModel.setName(txtName.getValue());
		roleOrganizationExpandsModel.setDescription(txtDesr.getValue());
		if(checkNotNull) {
			roleOrganizationExpandsModel.setPermissionKeys(roleOrgModel.getPermissionKeys());
			roleOrganizationExpandsModel.setUserIds(roleOrgModel.getUserIds());
			if(checkAuthority) {
				if(cbAutoUpdate.getValue() == false) {
					roleOrganizationExpandsModel.setRoleTemplateId(null);
				}
			}
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
			ApiResultResponse<Object> createRole = ApiOrganizationService.createRole(parentId, apiRoleOrganizationExpandsModel);
			if(createRole.getStatus() == 200) {
				onRun.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void doUpdateRole(RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
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

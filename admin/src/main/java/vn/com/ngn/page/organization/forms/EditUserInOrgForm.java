package vn.com.ngn.page.organization.forms;

import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiUserGeneraModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.forms.details.DetailRoleOfUserForm;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;
import vn.com.ngn.page.organization.models.UserGeneraModel;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DialogTemplate;

public class EditUserInOrgForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	private TextField txtPositionName = new TextField("Chức vụ");
	private TextField txtAccountOffice = new TextField("Tài khoản kết nối hệ thống văn bản");
	private Checkbox cbActive = new Checkbox("Hoạt động");
	private ButtonTemplate btnEditRole = new ButtonTemplate("Chỉnh sửa vai trò",FontAwesome.Solid.CHALKBOARD_TEACHER.create());

	private UserGeneraModel userGeneraModel;

	private String parentId,orgId;
	private Runnable onRun;
	private List<RoleOrganizationExpandsModel> listRoleOfUser;
	public EditUserInOrgForm(String parentId,String orgId,List<RoleOrganizationExpandsModel> listRoleOfUser,Runnable onRun) {
		this.onRun = onRun;
		this.parentId = parentId;
		buildLayout();
		configComponent();
		if(orgId!=null) {
			this.orgId = orgId;
			loadData();
		}
		if(listRoleOfUser != null) {
			this.listRoleOfUser = listRoleOfUser;
		}
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createLayout());
		
	}

	@Override
	public void configComponent() {
		btnEditRole.addClickListener(e->{
			openDialogPermission(orgId, listRoleOfUser);
		});
	}
	
	public void loadData() {
		
		try {
			ApiResultResponse<ApiUserGeneraModel> getResponse = ApiOrganizationService.getAUserOfOrg(parentId, orgId);
			userGeneraModel = new UserGeneraModel(getResponse.getResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
		txtPositionName.setValue(userGeneraModel.getPositionName() == (null) ? "" : userGeneraModel.getPositionName().toString());
		txtAccountOffice.setValue(userGeneraModel.getAccountIOffice() == (null) ? "" : userGeneraModel.getAccountIOffice().toString());
		cbActive.setValue(userGeneraModel.isActive());
	}
	
	private Component createLayout() {
		VerticalLayout vLayout = new VerticalLayout();
		
		txtAccountOffice.setSizeFull();
		txtPositionName.setSizeFull();
		
		btnEditRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		cbActive.getStyle().set("margin-top", "9px");
		
		HorizontalLayout hLayoutEdit = new HorizontalLayout();
		hLayoutEdit.add(btnEditRole,cbActive);
		
		vLayout.add(txtPositionName,txtAccountOffice,hLayoutEdit);
		vLayout.setWidthFull();
		
		return vLayout;
	}
	
	public void updateUser() {
		UserGeneraModel userGeneraModel = new UserGeneraModel();
		userGeneraModel.setAccountIOffice(txtAccountOffice.getValue());
		userGeneraModel.setPositionName(txtPositionName.getValue());
		userGeneraModel.setActive(cbActive.getValue());
		doUpdateUser(userGeneraModel);
	}
	
	private void doUpdateUser(UserGeneraModel userGeneraModel) {
		try {
			ApiUserGeneraModel apiUserGeneraModel = new ApiUserGeneraModel(userGeneraModel);
			ApiResultResponse<Object> data = ApiOrganizationService.updateUserOfOrg(parentId, orgId, apiUserGeneraModel);
			if(data.getStatus() == 200) {
				onRun.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openDialogPermission(String idUser,List<RoleOrganizationExpandsModel> listRoleOfUser) {
		DialogTemplate dialog = new DialogTemplate("Danh sách vai trò của người dùng",()->{
		});
		
		DetailRoleOfUserForm detailRoleOfUserForm = new DetailRoleOfUserForm(parentId,idUser,listRoleOfUser);
		dialog.add(detailRoleOfUserForm);
	
		dialog.getBtnSave().setVisible(false);
		dialog.setSizeFull();
		dialog.open();
	}
	
	
}

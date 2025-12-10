package vn.com.ngn.page.organization.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.api.organization.ApiUserGeneraModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;
import vn.com.ngn.page.organization.models.UserGeneraModel;
import vn.com.ngn.utils.components.NotificationTemplate;

public class UserInRoleForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private Grid<UserGeneraModel> grid = new Grid<UserGeneraModel>(UserGeneraModel.class,false);
	private List<UserGeneraModel> listModel = new ArrayList<UserGeneraModel>();
	private List<UserGeneraModel> listSelected  = new ArrayList<UserGeneraModel>();

	private Button btnRemoveUserFormRole = new Button("Xóa người dùng ra khỏi vai trò",FontAwesome.Solid.ARROW_RIGHT.create());
	
	private RoleOrganizationExpandsModel roleOrganizationExpandsModel = new RoleOrganizationExpandsModel();
	
	private String parentId,roleId;
	public UserInRoleForm(String parentId,String roleId) {
		this.parentId = parentId;
		this.roleId = roleId;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		btnRemoveUserFormRole.setEnabled(false);
		btnRemoveUserFormRole.getStyle().setCursor("pointer");
		this.add(btnRemoveUserFormRole, createGrid());
	}

	@Override
	public void configComponent() {
		btnRemoveUserFormRole.addClickListener(e->{
			updateUser();
		});
	}
	
	public void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiUserGeneraModel>> listData = ApiOrganizationService.getListUserInRole(parentId, roleId);
			listModel = listData.getResult().stream().map(UserGeneraModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		grid.setItems(listModel);
	}
	
	private Component createGrid() {
		grid = new Grid<UserGeneraModel>(UserGeneraModel.class,false);
		
		grid.addColumn(UserGeneraModel::getFullName).setHeader("Họ và Tên");
		grid.addColumn(UserGeneraModel::getPositionName).setHeader("Chức vụ ");
		
		grid.setSelectionMode(Grid.SelectionMode.MULTI);
		grid.addSelectionListener(e->{
			listSelected.clear();
			listSelected.addAll(e.getAllSelectedItems());
			if(e.getAllSelectedItems().isEmpty()) {
				btnRemoveUserFormRole.setEnabled(false);
			}else {
				btnRemoveUserFormRole.setEnabled(true);
			}
		});
		
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		
		return grid;
	}
	
	
	public void updateUser() {
		
		try {
			ApiResultResponse<ApiRoleOrganizationExpandsModel> data = ApiOrganizationService.getOneRole(parentId, roleId);
			roleOrganizationExpandsModel = new RoleOrganizationExpandsModel(data.getResult());
			for(UserGeneraModel userGeneraModel : listSelected) {
				listModel.remove(userGeneraModel);
			}
			roleOrganizationExpandsModel.getUserIds().clear();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		listModel.forEach(model->{
			roleOrganizationExpandsModel.getUserIds().add(model.getUserId());
		});
		
		doUpdate(roleOrganizationExpandsModel);
	}
	
	private void doUpdate(RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
		try {
			ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel = new ApiRoleOrganizationExpandsModel(roleOrganizationExpandsModel);
			ApiResultResponse<Object> updateRole = ApiOrganizationService.updateRole(parentId, roleId, apiRoleOrganizationExpandsModel);
			if(updateRole.getStatus() == 200) {
				loadData();
				NotificationTemplate.success("Thành công");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

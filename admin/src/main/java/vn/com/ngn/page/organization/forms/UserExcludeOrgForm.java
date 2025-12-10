package vn.com.ngn.page.organization.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.ListUserIDsModel;
import vn.com.ngn.page.user.model.UserModel;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class UserExcludeOrgForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	
	private Grid<UserModel> grid = new Grid<UserModel>(UserModel.class,false);
	private List<UserModel> listModel = new ArrayList<UserModel>();
	private List<UserModel> listUserSelected = new ArrayList<UserModel>();
	
	private Button btnMoveIncludeOrg = new Button("Di chuyển người dùng vào đơn vị",FontAwesome.Solid.ARROW_LEFT.create());
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());
	private TextField txtSearch = new TextField();
	
	private String parentId;
	public UserExcludeOrgForm(String parentId) {
		this.parentId = parentId;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		btnMoveIncludeOrg.setEnabled(false);
		this.add(createToolbar(),createGrid());
		
	}

	@Override
	public void configComponent() {
		txtSearch.addValueChangeListener(e->{
			loadData();
		});
		
		btnSearch.addClickListener(e->{
			loadData();
		});
		btnMoveIncludeOrg.addClickListener(e->{
			moveUserToOrg();
		});
	}
	
	public void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiUserModel>> listUser = ApiOrganizationService.getListUserExcludeOrg(parentId, txtSearch.getValue());
			listModel = listUser.getResult().stream().map(UserModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		grid.setItems(listModel);
		
	}
	
	private Component createGrid() {
		grid = new Grid<UserModel>(UserModel.class,false);
		
		grid.addColumn(UserModel::getUsername).setHeader("Tên đăng nhập");
		grid.addColumn(UserModel::getFullName).setHeader("Họ và Tên");
		grid.addColumn(UserModel::getEmail).setHeader("Email");
		
		grid.setSelectionMode(Grid.SelectionMode.MULTI);
		grid.addSelectionListener(e->{
			listUserSelected.clear();
			
			listUserSelected.addAll(e.getAllSelectedItems());
			if(e.getAllSelectedItems().isEmpty()) {
				btnMoveIncludeOrg.setEnabled(false);
			}else {
				btnMoveIncludeOrg.setEnabled(true);
			}
		});
		
		grid.setSizeFull();
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		
		return grid;
	}
	
	private Component createToolbar() {
		VerticalLayout vLayout = new VerticalLayout();
		HorizontalLayout hLayout = new HorizontalLayout();
		
		txtSearch.setSizeFull();
		txtSearch.setPlaceholder("Nhập tên người dùng để tìm...");
		txtSearch.setClearButtonVisible(true);
		
		
		hLayout.expand(txtSearch);
		hLayout.add(btnMoveIncludeOrg,txtSearch,btnSearch);
		
		vLayout.add(hLayout);
		btnMoveIncludeOrg.setTooltipText("Đưa người dùng vào tổ chức ");
		
		hLayout.setSizeFull();
		vLayout.setWidthFull();
		return vLayout;
	}
	
	private void moveUserToOrg() {
		try {
			ListUserIDsModel listUserIDsModel = new ListUserIDsModel();
			listUserSelected.forEach(model->{
				listUserIDsModel.getUserIds().add(model.getId());
			});
			
			ApiResultResponse<Object> moveUser = ApiOrganizationService.moveUsersToOrg(parentId, listUserIDsModel);
			if(moveUser.getStatus()==200) {
				NotificationTemplate.success("Di chuyển người dùng thành công");
				loadData();
				fireEvent(new ClickEvent(this, false));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
























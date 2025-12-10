package vn.com.ngn.page.organization.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiGroupOrganizationExpandsModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiUserGeneraModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.GroupOrganizationExpandsModel;
import vn.com.ngn.page.organization.models.UserGeneraModel;
import vn.com.ngn.utils.components.NotificationTemplate;

public class UserIncludeGroupForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private Grid<UserGeneraModel> grid = new Grid<UserGeneraModel>(UserGeneraModel.class,false);
	private List<UserGeneraModel> listModel = new ArrayList<UserGeneraModel>();
	private List<UserGeneraModel> listUserSelected = new ArrayList<UserGeneraModel>();
	
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());
	private TextField txtSearch = new TextField();
	private Button btnRemoveUsersFromGroup = new Button("Xóa người dùng ra khỏi tổ chức",FontAwesome.Solid.ARROW_RIGHT.create());
	
	
	private String parentId;
	private String idGroup;
	public UserIncludeGroupForm(String parentId,String idGroup) {
		this.parentId = parentId;
		this.idGroup = idGroup;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		btnRemoveUsersFromGroup.getStyle().setCursor("pointer");
		btnRemoveUsersFromGroup.setEnabled(false);
		this.add(btnRemoveUsersFromGroup,createGrid());
		
	}

	@Override
	public void configComponent() {
		btnRemoveUsersFromGroup.addClickListener(e->{
			saveUserGroup();
		});
		
		
	}
	
	public void loadData() {
		try {
			ApiResultResponse<List<ApiUserGeneraModel>> listData = ApiOrganizationService.getListUserInGroup(parentId, idGroup);
			listModel = listData.getResult().stream().map(UserGeneraModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		grid.setItems(listModel);
		
	}
	
	private Component createGrid() {
		grid = new Grid<UserGeneraModel>(UserGeneraModel.class,false);
		
		grid.addColumn(UserGeneraModel::getFullName).setHeader("Họ và Tên");
		grid.addColumn(UserGeneraModel::getPositionName).setHeader("Chức vụ");
		
		grid.setSelectionMode(Grid.SelectionMode.MULTI);
		grid.addSelectionListener(e->{
			listUserSelected.clear();
			listUserSelected.addAll(e.getAllSelectedItems());
			if(e.getAllSelectedItems().isEmpty()) {
				btnRemoveUsersFromGroup.setEnabled(false);
			}else {
				btnRemoveUsersFromGroup.setEnabled(true);
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
	
	@SuppressWarnings("unused")
	private Component createToolbar() {
		VerticalLayout vLayout = new VerticalLayout();
		HorizontalLayout hLayout = new HorizontalLayout();
		
		btnRemoveUsersFromGroup.getStyle().setCursor("pointer");
		btnRemoveUsersFromGroup.setEnabled(false);
		
		txtSearch.setPlaceholder("Nhập từ khóa để lọc");
		
		btnSearch.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSearch.getStyle().setCursor("pointer");
		
		hLayout.expand(txtSearch);
		hLayout.add(txtSearch,btnSearch);
		hLayout.setWidthFull();
		
		vLayout.add(btnRemoveUsersFromGroup,hLayout);
		vLayout.setWidthFull();
		
		return vLayout;
	}
	
	public void saveUserGroup() {
		GroupOrganizationExpandsModel groupOrganizationExpandsModel = new GroupOrganizationExpandsModel();
		try {
			ApiResultResponse<ApiGroupOrganizationExpandsModel> group = ApiOrganizationService.getAGroup(parentId, idGroup);
			groupOrganizationExpandsModel.setName(group.getResult().getName());
			groupOrganizationExpandsModel.setDescription(group.getResult().getDescription());
			groupOrganizationExpandsModel.setOrder(group.getResult().getOrder());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(UserGeneraModel userOfGroupModel : listUserSelected) {
			listModel.remove(userOfGroupModel);
		}
		
		listModel.forEach(model->{
			groupOrganizationExpandsModel.getUserIds().add(model.getUserId());
		});
		
		updateGroup(groupOrganizationExpandsModel);
		
	}
	
	public void updateGroup(GroupOrganizationExpandsModel groupOrganizationExpandsModel) {
		try {
			
			ApiGroupOrganizationExpandsModel apiGroupOrganizationExpandsModel = new ApiGroupOrganizationExpandsModel(groupOrganizationExpandsModel);
			
			ApiResultResponse<Object> updateGroup = ApiOrganizationService.updateGroup(parentId, idGroup, apiGroupOrganizationExpandsModel);
			if(updateGroup.getStatus()==200) {
				NotificationTemplate.success("Thành công");
				loadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

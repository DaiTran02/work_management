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

public class UserExcludeGroupForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private TextField txtSearch = new TextField();
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());
	private Button btnMoveUsersToGroup = new Button("Di chuyển người dùng vào tổ",FontAwesome.Solid.ARROW_LEFT.create());
	
	private Grid<UserGeneraModel> grid = new Grid<UserGeneraModel>(UserGeneraModel.class,false);
	private List<UserGeneraModel> listModel = new ArrayList<UserGeneraModel>();
	private List<UserGeneraModel> listUsersIsSelected = new ArrayList<UserGeneraModel>();

	private String parentId;
	private String groupId;
	public UserExcludeGroupForm(String parentId,String groupId) {
		this.parentId = parentId;
		this.groupId = groupId;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		btnMoveUsersToGroup.getStyle().setCursor("pointer");
		btnMoveUsersToGroup.setEnabled(false);
		this.add(btnMoveUsersToGroup,createGrid());
		
	}

	@Override
	public void configComponent() {
		btnMoveUsersToGroup.addClickListener(e->{
			saveGroup();
		});
	}
	
	public void loadData() {
		listModel.clear();
		try {
			//Get all list users is not parentId
			ApiResultResponse<List<ApiUserGeneraModel>> listUser = ApiOrganizationService.getListUserNotInAllGroup(parentId);
			listModel = listUser.getResult().stream().map(UserGeneraModel::new).collect(Collectors.toList());
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
			listUsersIsSelected.clear();
			listUsersIsSelected.addAll(e.getAllSelectedItems());
			if(e.getAllSelectedItems().isEmpty()) {
				btnMoveUsersToGroup.setEnabled(false);
			}else {
				btnMoveUsersToGroup.setEnabled(true);
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
		
		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Nhập từ khóa để lọc");
		
		btnSearch.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSearch.getStyle().setCursor("pointer");
		
		hLayout.add(txtSearch,btnSearch);
		hLayout.expand(txtSearch);
		hLayout.setWidthFull();
		
		btnMoveUsersToGroup.getStyle().setCursor("pointer");
		btnMoveUsersToGroup.setEnabled(false);
		
		vLayout.add(btnMoveUsersToGroup,hLayout);
		vLayout.setWidthFull();
		
		return vLayout;
	}
	
	public void saveGroup() {
		GroupOrganizationExpandsModel groupOrganizationExpandsModel = new GroupOrganizationExpandsModel();
		List<String> listUserIds = new ArrayList<String>();
		try {
			ApiResultResponse<ApiGroupOrganizationExpandsModel> group = ApiOrganizationService.getAGroup(parentId, groupId);
			groupOrganizationExpandsModel.setName(group.getResult().getName());
			groupOrganizationExpandsModel.setDescription(group.getResult().getDescription());
			groupOrganizationExpandsModel.setOrder(group.getResult().getOrder());
			for(String idUser : group.getResult().getUserIds()) {
				listUserIds.add(idUser);
			}
			listUsersIsSelected.forEach(model->{
				listUserIds.add(model.getUserId());
			});
			groupOrganizationExpandsModel.setUserIds(listUserIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		updateGroup(groupOrganizationExpandsModel);
	}
	
	public void updateGroup(GroupOrganizationExpandsModel groupOrganizationExpandsModel) {
		try {
			
			ApiGroupOrganizationExpandsModel apiGroupOrganizationExpandsModel = new ApiGroupOrganizationExpandsModel(groupOrganizationExpandsModel);
			
			ApiResultResponse<Object> updateGroup = ApiOrganizationService.updateGroup(parentId, groupId, apiGroupOrganizationExpandsModel);
			if(updateGroup.getStatus()==200) {
				NotificationTemplate.success("Thành công");
				loadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package vn.com.ngn.page.user.form.details;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.ldap.ApiLdapService;
import vn.com.ngn.api.ldap.ApiListUserLdapModel;
import vn.com.ngn.api.ldap.ApiUserFilterLdapModel;
import vn.com.ngn.api.user.ApiImportUsersLdapToSystemModel;
import vn.com.ngn.api.user.ApiResultUserImportFromLdapModel;
import vn.com.ngn.api.user.ApiUserFilter;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.api.user.ApiUserService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.PaginationForm;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class ImportUserFromLdapToSystemForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	
	private TextField txtSearch = new TextField();
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm",FontAwesome.Solid.SEARCH.create());
	private ButtonTemplate btnAddUser = new ButtonTemplate("Thêm vào hệ thống",FontAwesome.Solid.PLUS.create());
	
	
	private PaginationForm paginationForm;
	
	private List<String> listUserOfSystem = new ArrayList<String>();
	private HashMap<String, String> mapUserOfSystem = new HashMap<String, String>();
	
	
	private Grid<ApiListUserLdapModel> grid = new Grid<ApiListUserLdapModel>(ApiListUserLdapModel.class,false);
	private List<ApiListUserLdapModel> listModel = new ArrayList<ApiListUserLdapModel>();
	private List<String> usernameIsChoose = new ArrayList<String>();
	
	
	public ImportUserFromLdapToSystemForm() {
		buildLayout();
		configComponent();
		loadDataUserOfSystem();
		loadData();
	}

	@Override
	public void buildLayout() {
		paginationForm = new PaginationForm(()->{
			if(paginationForm != null) {
				loadData();
			}
		});
		
		btnAddUser.setEnabled(false);
		
		this.add(createLayoutFilter(),paginationForm,createGrid());
		
	}
	


	@Override
	public void configComponent() {
		btnAddUser.addClickListener(e->{
			doImportToSystem();
		});
		
		btnSearch.addClickListener(e->loadData());
		btnSearch.addClickShortcut(Key.ENTER);
		
		txtSearch.addValueChangeListener(e->loadData());
		
	}
	
	private void loadData() {
		listModel = new ArrayList<ApiListUserLdapModel>();
		try {
			ApiResultResponse<List<ApiListUserLdapModel>> data = ApiLdapService.searchUsers(getSearch());
			if(data.isSuscces()) {
				listModel.addAll(data.getResult());
				paginationForm.setItemCount(data.getTotal());
			}
		} catch (Exception e) {
		}
		grid.setItems(listModel);
	}
	
	private void loadDataUserOfSystem() {
		listUserOfSystem = new ArrayList<String>();
		ApiUserFilter apiUserFilter = new ApiUserFilter();
		apiUserFilter.setLimit(0);
		apiUserFilter.setSkip(0);
		apiUserFilter.setActive(true);
		try {
			ApiResultResponse<List<ApiUserModel>> data = ApiUserService.getAllUser(apiUserFilter);
			if(data.isSuscces()) {
				data.getResult().forEach(model->{
					listUserOfSystem.add(model.getUsername());
					mapUserOfSystem.put(model.getUsername(), model.getFullName());
				});
			}
		} catch (Exception e) {
		}
	}
	
	private Grid<ApiListUserLdapModel> createGrid() {
		grid = new Grid<ApiListUserLdapModel>(ApiListUserLdapModel.class,false);
		
		grid.addComponentColumn(model->{
			Checkbox cbChooseUser = new Checkbox();
			
			cbChooseUser.addClickListener(e->{
				if(cbChooseUser.getValue()) {
					usernameIsChoose.add(model.getUsername());
				}else {
					usernameIsChoose.remove(model.getUsername());
				}
				if(!usernameIsChoose.isEmpty()) {
					btnAddUser.setEnabled(true);
				}
			});
			
			
			if(checkUser(model.getUsername())) {
				cbChooseUser.setValue(true);
				cbChooseUser.setReadOnly(true);
				cbChooseUser.setTooltipText("Người dùng này đã được đưa vào hệ thống");
			}
			
//			if(searchUtils.binarySearch(listUserOfSystem, model.getUsername()) == true) {
//				cbChooseUser.setValue(true);
//			}
			
			return cbChooseUser;
		}).setWidth("70px").setFlexGrow(0);
		grid.addColumn(ApiListUserLdapModel::getFullName).setHeader("Tên đầy đủ");
		grid.addColumn(ApiListUserLdapModel::getUsername).setHeader("Username");
		grid.addColumn(ApiListUserLdapModel::getMailText).setHeader("Mail");
		grid.addColumn(ApiListUserLdapModel::getMobileText).setHeader("Số điện thoại");
		
		return grid;
	}
	
	private Component createLayoutFilter() {
		HorizontalLayout hLayoutFilter = new HorizontalLayout();
		hLayoutFilter.setWidthFull();
		
		hLayoutFilter.add(btnAddUser,txtSearch,btnSearch);
		hLayoutFilter.expand(txtSearch);
		
		txtSearch.setPlaceholder("Nhập từ khóa để tìm...");
		txtSearch.setClearButtonVisible(true);
		
		
		return hLayoutFilter;
	}
	
	private boolean checkUser(String userName) {
//		for(String i : listUserOfSystem) {
//			if(i.equals(userName)) {
//				return true;
//			}
//		}
		
		if(mapUserOfSystem.containsKey(userName)) {
			return true;
		}
		
		return false;
	}
	
	private void doImportToSystem() {
		ApiImportUsersLdapToSystemModel apiImportUsersLdapToSystemModel = new ApiImportUsersLdapToSystemModel();
		apiImportUsersLdapToSystemModel.setUsernames(usernameIsChoose);
		try {
			ApiResultResponse<List<ApiResultUserImportFromLdapModel>> dataImport = ApiUserService.importUserToSystemFromLdap(apiImportUsersLdapToSystemModel);
			if(dataImport.isSuscces()) {
				NotificationTemplate.success("Thêm người dùng vào hệ thống thành công");
			}
		} catch (Exception e) {
		}
	}
	
	private ApiUserFilterLdapModel getSearch() {
		ApiUserFilterLdapModel apiUserFilterLdapModel = new ApiUserFilterLdapModel();
		
		apiUserFilterLdapModel.setLimit(paginationForm.getLimit());
		apiUserFilterLdapModel.setSkip(paginationForm.getSkip());
		apiUserFilterLdapModel.setUsername(txtSearch.getValue());
		
		return apiUserFilterLdapModel;
	}
}

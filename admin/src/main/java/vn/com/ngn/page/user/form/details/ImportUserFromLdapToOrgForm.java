package vn.com.ngn.page.user.form.details;

import java.util.ArrayList;
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
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.user.ApiImportUsersLdapToSystemModel;
import vn.com.ngn.api.user.ApiResultUserImportFromLdapModel;
import vn.com.ngn.api.user.ApiUserFilter;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.api.user.ApiUserService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.ListUserIDsModel;
import vn.com.ngn.page.organization.models.OrganizationModel;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.PaginationForm;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class ImportUserFromLdapToOrgForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private PaginationForm paginationForm;
	private ButtonTemplate btnAddUser = new ButtonTemplate("Thêm vào đơn vị",FontAwesome.Solid.PLUS.create());
	private TextField txtOrg = new TextField();
	private ButtonTemplate btnOrg = new ButtonTemplate("Chọn đơn vị");
	private TextField txtSearch = new TextField();
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm",FontAwesome.Solid.SEARCH.create());
	
	private OrganizationModel organizationModel;
	private ListOrgToImportUserForm listOrgToImportUserForm;
	
	private List<String> listUserOfSystem = new ArrayList<String>();
	private Grid<ApiListUserLdapModel> grid = new Grid<ApiListUserLdapModel>(ApiListUserLdapModel.class,false);
	private List<ApiListUserLdapModel> listModel = new ArrayList<ApiListUserLdapModel>();
	private List<String> usernameIsChoose = new ArrayList<String>();
	private List<ApiResultUserImportFromLdapModel> listUserIsImportToSystem = new ArrayList<ApiResultUserImportFromLdapModel>();
	
	
	public ImportUserFromLdapToOrgForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		btnAddUser.setEnabled(false);
		paginationForm = new PaginationForm(()->{
			if(paginationForm != null)
				loadData();
		});
		
		this.add(createFilter(),paginationForm,createGrid());
		
	}

	@Override
	public void configComponent() {
		btnAddUser.addClickListener(e->{
			doImportToSystem();
		});
		
		btnOrg.addClickListener(e->{
			openDialogChooseOrgToImport();
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
				if(!usernameIsChoose.isEmpty() &&  organizationModel != null) {
					btnAddUser.setEnabled(true);
				}
			});
			
			
			if(checkUser(model.getUsername())) {
				cbChooseUser.setValue(true);
				cbChooseUser.setTooltipText("Người dùng này đã được đưa vào đơn vị");
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
	
	private void loadDataUserOfSystem() {
		listUserOfSystem = new ArrayList<String>();
		ApiUserFilter apiUserFilter = new ApiUserFilter();
		apiUserFilter.setLimit(0);
		apiUserFilter.setSkip(0);
		apiUserFilter.setActive(true);
		if(organizationModel.getId() != null) {
			apiUserFilter.setIncludeOrganizationId(organizationModel.getId());
		}
		try {
			ApiResultResponse<List<ApiUserModel>> data = ApiUserService.getAllUser(apiUserFilter);
			if(data.isSuscces()) {
				data.getResult().forEach(model->{
					listUserOfSystem.add(model.getUsername());
				});
			}
		} catch (Exception e) {
		}
	}
	
	private Component createFilter() {
		HorizontalLayout hLayoutFilter = new HorizontalLayout();
		
		hLayoutFilter.setWidthFull();
		
		txtOrg.setWidthFull();
		txtOrg.setPlaceholder("Chọn đơn vị để thêm người dùng");
		txtOrg.setReadOnly(true);
		txtOrg.setValue("Chọn đơn vị để thêm người dùng");
		
		
		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Tìm kiếm username...");
		
		btnAddUser.setEnabled(false);
		
		hLayoutFilter.add(btnAddUser,txtOrg,btnOrg,txtSearch,btnSearch);
		
		hLayoutFilter.getStyle().set("align-items", "center");
		
		return hLayoutFilter;
	}
	
	private boolean checkUser(String userName) {
		for(String i : listUserOfSystem) {
			if(i.equals(userName)) {
				return true;
			}
		}
		return false;
	}
	
	private void doImportToSystem() {
		ApiImportUsersLdapToSystemModel apiImportUsersLdapToSystemModel = new ApiImportUsersLdapToSystemModel();
		apiImportUsersLdapToSystemModel.setUsernames(usernameIsChoose);
		try {
			ApiResultResponse<List<ApiResultUserImportFromLdapModel>> dataImport = ApiUserService.importUserToSystemFromLdap(apiImportUsersLdapToSystemModel);
			if(dataImport.isSuscces()) {
				dataImport.getResult().forEach(model->{
					listUserIsImportToSystem.add(model);
				});
				doImportToOrg();
			}
		} catch (Exception e) {
		}
	}
	
	private void doImportToOrg() {
		try {
			ListUserIDsModel listUserIDsModel = new ListUserIDsModel();
			listUserIsImportToSystem.forEach(model->{
				listUserIDsModel.getUserIds().add(model.getId());
			});
			
			ApiResultResponse<Object> moveUser = ApiOrganizationService.moveUsersToOrg(organizationModel.getId(), listUserIDsModel);
			if(moveUser.getStatus()==200) {
				NotificationTemplate.success("Đã thêm người dùng vào hệ thống và đơn vị thành công");
				loadDataUserOfSystem();
				loadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openDialogChooseOrgToImport() {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn đơn vị để thêm người dùng vào",()->{
			
		});
		
		listOrgToImportUserForm = new ListOrgToImportUserForm(null, ()->{
			organizationModel = listOrgToImportUserForm.getOrg();
			txtOrg.setValue(organizationModel.getName());
			loadDataUserOfSystem();
			loadData();
			dialogTemplate.close();
			if(!usernameIsChoose.isEmpty() && organizationModel != null) {
				btnAddUser.setEnabled(true);
			}
		});
		
		listOrgToImportUserForm.addChangeListener(e->{
			organizationModel = listOrgToImportUserForm.getOrg();
			txtOrg.setValue(organizationModel.getName());
		});
		
		dialogTemplate.add(listOrgToImportUserForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
	}
	
	private ApiUserFilterLdapModel getSearch() {
		ApiUserFilterLdapModel apiUserFilterLdapModel = new ApiUserFilterLdapModel();
		
		apiUserFilterLdapModel.setLimit(paginationForm.getLimit());
		apiUserFilterLdapModel.setSkip(paginationForm.getSkip());
		apiUserFilterLdapModel.setUsername(txtSearch.getValue());
		
		return apiUserFilterLdapModel;
	}
	
}

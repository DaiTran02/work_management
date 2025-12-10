package vn.com.ngn.page.user.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.user.ApiUserFilter;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.api.user.ApiUserService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.user.model.BelongOrganizationsModel;
import vn.com.ngn.page.user.model.UserModel;
import vn.com.ngn.utils.CheckPermissionUtil;
import vn.com.ngn.utils.SessionUtil;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.PaginationForm;

public class UserForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private CheckPermissionUtil checkPermissionUtil = new CheckPermissionUtil();
	private Grid<UserModel> grid = new Grid<UserModel>(UserModel.class,false);
	private List<UserModel> listModels = new ArrayList<UserModel>();
	private ListDataProvider<UserModel> listDataProvider = new ListDataProvider<UserModel>(listModels);


	private TextField txtSearch = new TextField();
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());
	private Button btnAddUser = new Button("Thêm người dùng",FontAwesome.Solid.PLUS.create());
	private ButtonTemplate btnImportUserFromLdap = new ButtonTemplate("Thêm người dùng từ LDAP",FontAwesome.Solid.FILE_IMPORT.create());
	private Checkbox cbMyOrg = new Checkbox("Đơn vị tôi quản lý");
	private Checkbox cbActive = new Checkbox("Hoạt động");
	private ComboBox<Pair<String, String>> cmbReviewOrg = new ComboBox<Pair<String,String>>();
	private ComboBox<Pair<String, String>> cmbProvider = new ComboBox<Pair<String,String>>();
	private ComboBox<Pair<String, String>> cmbOrgOfUser = new ComboBox<Pair<String,String>>();
	private PaginationForm paginationForm;

	public UserForm() {
		initCmbReviewOrg();
		buildLayout();
		configComponent();
		checkPermission();
		initCmbProvider();
		initCmbOrgUser();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.setPadding(false);
		this.add(createToolbar());
		paginationForm = new PaginationForm(()->{
			if(paginationForm!=null) {
				loadData();
			}
		});
		this.add(paginationForm);
		this.add(createGrid());
	}

	@Override
	public void configComponent() {
		btnAddUser.addClickListener(e->{
			openDialogCreateUser();
		});

		txtSearch.addValueChangeListener(e->{
			loadData();
		});
		
		cbActive.addClickListener(e->loadData());
		

		btnSearch.addClickListener(e->{
			loadData();
		});
		btnSearch.addClickShortcut(Key.ENTER);
		
		btnImportUserFromLdap.addClickListener(e->{
			openDialogImportUserFromLdap();
		});
		
		cmbReviewOrg.addValueChangeListener(e->loadData());
		cmbOrgOfUser.addValueChangeListener(e->loadData());
		cmbProvider.addValueChangeListener(e->loadData());
	}

	private void loadData() {
		try {
			ApiResultResponse<List<ApiUserModel>> listDataApi = ApiUserService.getAllUser(getSearch());
			paginationForm.setItemCount(listDataApi.getTotal());
			listModels=listDataApi.getResult().stream().map(UserModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		listDataProvider = new ListDataProvider<UserModel>(listModels);
		grid.setItems(listDataProvider);
	}

	private void checkPermission() {
		if(checkPermissionUtil.checkOrg() == false) {
			cbMyOrg.setVisible(false);
		}else {
			cbMyOrg.setVisible(true);
			cbMyOrg.setValue(true);
		}
	}


	private Component createGrid() {
		grid = new Grid<UserModel>(UserModel.class,false);

		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

		grid.addColumn(UserModel::getUsername).setHeader("Tên đăng nhập");
		grid.addColumn(UserModel::getFullName).setHeader("Họ và Tên");
		grid.addColumn(UserModel::getEmail).setHeader("Email");
		grid.addColumn(UserModel::getActiveCode).setHeader("Mã kích hoạt").setWidth("120px").setFlexGrow(0);
		grid.addColumn(model->{
			return model.getProvider().getName();
		}).setHeader("Provider");
		
		grid.addComponentColumn(model->{
			if(model.isActive()) {
				return createStatusIcon("Available");
			}else {
				return createStatusIcon("NotAvailable");
			}
		}).setHeader("Tình trạng").setWidth("150px").setFlexGrow(0);
		
		grid.addComponentColumn(model->{
			ButtonTemplate btnReview = new ButtonTemplate();
			
			if(model.getFirstReview() != null) {
				if(model.getFirstReview().isReviewed()){
					btnReview.setText("Đã duyệt");
					btnReview.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
				}else {
					btnReview.setText("Tự chọn (Chưa duyệt)");
					btnReview.addThemeVariants(ButtonVariant.LUMO_ERROR);
				}
			}else {
				btnReview.setText("Quản trị gán đơn vị");
				btnReview.setEnabled(false);
			}
			
			btnReview.addClickListener(e->openDialogReview(model.getId()));
			
			btnReview.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			
			return btnReview;
		}).setHeader("Gán đơn vị").setWidth("160px").setFlexGrow(0);
		
		grid.addComponentColumn(model->{
			ButtonTemplate btnOrg = new ButtonTemplate("Đơn vị sử dụng ("+model.getBelongOrganizations().size()+")");
			btnOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnOrg.addClickListener(e->{
				openDialogOrg(model.getId(),model.getBelongOrganizations());
			});
			return btnOrg;
		}).setHeader("Đơn vị").setWidth("160px").setFlexGrow(0);

		grid.addComponentColumn(model->{
			Button btnEdit = new Button(FontAwesome.Solid.EDIT.create());
			btnEdit.setEnabled(false);
			btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEdit.getStyle().setCursor("pointer");
			btnEdit.setTooltipText("Chỉnh sửa");
			btnEdit.addClickListener(e->{
				openDialogUpdateUser(model.getId());
			});
			
			ButtonTemplate btnOverview = new ButtonTemplate(FontAwesome.Solid.EYE.create());
			btnOverview.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnOverview.setTooltipText("Thông tin chi tiết");
			btnOverview.addClickListener(e->openDialogViewInFoUser(model.getId()));
			
			Button btnResetPassword = new Button(FontAwesome.Solid.TASKS_ALT.create());
			btnResetPassword.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnResetPassword.getStyle().setCursor("pointer");
			btnResetPassword.setTooltipText("Reset mật khẩu");
			btnResetPassword.addClickListener(e->{
				openDialogResetPassword(model.getId());
			});
			btnResetPassword.setEnabled(false);

			Button btnDelete = new Button(FontAwesome.Solid.TRASH.create());
			btnDelete.setEnabled(false);
			btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnDelete.getStyle().setCursor("pointer");
			btnDelete.setTooltipText("Xóa");

			btnDelete.addClickListener(e->{
				openDialogDeleteUser(model.getId());
			});

			if(checkPermissionUtil.checkCreator(model.getCreatorName())) {
				btnEdit.setEnabled(true);
				btnDelete.setEnabled(true);
			}
			
			if(SessionUtil.getUser().getUsername().equals("administrator") || SessionUtil.getUser().getUsername().equals("supper_admin")) {
				btnDelete.setEnabled(true);
			}
			
			if(!model.getBelongOrganizations().isEmpty()) {
				btnDelete.setEnabled(false);
			}
			
			if(model.getProvider().equals("local")) {
				btnResetPassword.setEnabled(true);
			}
			
			HorizontalLayout layout = new HorizontalLayout();
			layout.add(btnEdit,btnOverview,btnDelete);
			return layout;
		}).setHeader("Thao tác").setWidth("170px").setFlexGrow(0);

		grid.setWidthFull();

		return grid;

	}

	private Component createToolbar() {
		HorizontalLayout layout = new HorizontalLayout();

		txtSearch.setPlaceholder("Tìm kiếm.....");

		cbMyOrg.getStyle().set("margin-top", "5px");
		cbActive.getStyle().set("margin-top", "5px");
		
		cbActive.setValue(true);
		btnSearch.getStyle().setCursor("pointer");
		btnAddUser.getStyle().setCursor("pointer");
		
		cmbReviewOrg.setWidth("500px");
		cmbReviewOrg.setHelperText("Tìm những người dùng đăng nhập lần đầu");
		
		cmbProvider.setHelperText("Provider");
		
		cmbOrgOfUser.setHelperText("Gán đơn vị");
		
		
		txtSearch.setClearButtonVisible(true);

		layout.expand(txtSearch);
		layout.add(cmbReviewOrg,cmbProvider,cmbOrgOfUser,txtSearch,cbActive,btnSearch,btnAddUser,btnImportUserFromLdap);
		layout.setWidthFull();

		return layout;
	}

	private Button createStatusIcon(String status) {
		boolean isAvailable = "Available".equals(status);
		Button btnStatus;
		if (isAvailable) {
			btnStatus = new Button("Hoạt động",FontAwesome.Solid.CHECK.create());
			btnStatus.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		} else {
			btnStatus = new Button("Không hoạt động",FontAwesome.Solid.CLOSE.create());
			btnStatus.addThemeVariants(ButtonVariant.LUMO_ERROR);
		}
		btnStatus.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		return btnStatus;
	}
	
	private void initCmbReviewOrg() {
		List<Pair<String, String>> listReviewOrg = new ArrayList<Pair<String,String>>();
		listReviewOrg.add(Pair.of(null,"Tất cả"));
		listReviewOrg.add(Pair.of("1","Những người dùng đăng nhập lần đầu và tự chọn đơn vị"));
		listReviewOrg.add(Pair.of("2","Những người dùng đã được xem xét"));
		
		cmbReviewOrg.setItems(listReviewOrg);
		cmbReviewOrg.setItemLabelGenerator(Pair::getRight);
		cmbReviewOrg.setValue(listReviewOrg.get(0));
	}
	
	private void initCmbProvider() {
		List<Pair<String, String>> listProvider = new ArrayList<Pair<String,String>>();
		listProvider.add(Pair.of(null,"Tất cả"));
		listProvider.add(Pair.of("local","Local"));
		listProvider.add(Pair.of("ldap","Ldap"));
		
		cmbProvider.setItems(listProvider);
		cmbProvider.setItemLabelGenerator(Pair::getValue);
		cmbProvider.setValue(listProvider.get(0));
	}
	
	private void initCmbOrgUser() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		listData.add(Pair.of("false","Chưa gán đơn vị"));
		listData.add(Pair.of("true","Đã gán đơn vị"));
		
		cmbOrgOfUser.setItems(listData);
		cmbOrgOfUser.setItemLabelGenerator(Pair::getValue);
		cmbOrgOfUser.setValue(listData.get(0));
	}

	private void openDialogOrg(String idUser,List<BelongOrganizationsModel> listOrg) {
		DialogTemplate dialog = new DialogTemplate("DANH SÁCH ĐƠN VỊ",()->{

		});

		ListOrgInUserForm editUserForm = new ListOrgInUserForm(idUser,listOrg);
		
		editUserForm.addChangeListener(e->{
			loadData();
		});
		
		dialog.getBtnSave().addClickListener(e->{
			dialog.close();
		});

		dialog.getFooter().removeAll();
		
		dialog.add(editUserForm);
		dialog.setWidth("80%");
		dialog.setHeight("80%");

		dialog.open();
	}

	private void openDialogCreateUser() {
		DialogTemplate dialog = new DialogTemplate("THÊM TÀI KHOẢN NGƯỜI DÙNG",()->{

		});

		EditUserForm editUserForm = new EditUserForm(null,false,null,()->{
			loadData();
			NotificationTemplate.success("Thành công");
			dialog.close();
		});


		dialog.getBtnSave().addClickListener(e->{
			editUserForm.doSave();
		});

		dialog.add(editUserForm);
		dialog.setWidth("60%");

		dialog.open();
	}

	private void openDialogUpdateUser(String id) {
		DialogTemplate dialog = new DialogTemplate("CHỈNH SỬA TÀI KHOẢN NGƯỜI DÙNG",()->{

		});

		EditUserForm editUserForm = new EditUserForm(id,false,null, ()->{
			loadData();
			NotificationTemplate.success("Chỉnh sửa thành công");
			dialog.close();
		});


		dialog.getBtnSave().addClickListener(e->{
			editUserForm.doSave();
		});


		dialog.add(editUserForm);
		dialog.setWidth("60%");

		dialog.open();
	}

	private void openDialogResetPassword(String id) {
		DialogTemplate dialog = new DialogTemplate("ĐỔI MẬT KHẨU",()->{

		});

		ChangeAndResetPasswordForm chanPasswordForm = new ChangeAndResetPasswordForm(id, ()->{
			loadData();
			dialog.close();
		});


		dialog.getBtnSave().addClickListener(e->{
			chanPasswordForm.resetPassword();
		});


		dialog.add(chanPasswordForm);
		dialog.setWidth("40%");

		dialog.open();
	}
	
	private void openDialogViewInFoUser(String idUser) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chi tiết người dùng",()->{});
		
		DetailUserForm detailUserForm = new DetailUserForm(idUser);
		
		dialogTemplate.add(detailUserForm);
		
		
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeight("80%");
		dialogTemplate.open();
		dialogTemplate.getFooter().removeAll();
	}

	private void openDialogDeleteUser(String id) {
		ConfirmDialog confirmDialog = new ConfirmDialogTemplate("XÓA NGƯỜI DÙNG");
		confirmDialog.setText("Xác nhận xóa người dùng");
		confirmDialog.addConfirmListener(e->{
			doDeleteUser(id);
		});
		confirmDialog.setCancelable(true);
		confirmDialog.open();
	}
	
	private void openDialogReview(String idUser) {
		DialogTemplate dialogTemplate = new DialogTemplate("XEM XÉT NGƯỜI DÙNG",()->{});
		DetailFirstReviewForm detailFirstReviewForm = new DetailFirstReviewForm(idUser);
		
		detailFirstReviewForm.addChangeListener(e->{
			loadData();
		});
		
		dialogTemplate.add(detailFirstReviewForm);
		dialogTemplate.open();
		dialogTemplate.setWidth("40%");
		dialogTemplate.setHeight("auto");
		dialogTemplate.getFooter().removeAll();
	}
	
	private void openDialogImportUserFromLdap() {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm người dùng từ LDAP",()->{});
		
		TabImportUserFromLdapForm tabImportUserFromLdapForm = new TabImportUserFromLdapForm();
		dialogTemplate.add(tabImportUserFromLdapForm);
		
		dialogTemplate.setSizeFull();
		dialogTemplate.open();
		dialogTemplate.getFooter().removeAll();
	}

	private void doDeleteUser(String id) {
		try {
			ApiResultResponse<Object> delete = ApiUserService.deleteUser(id);
			System.out.println(delete);
			if(delete.getStatus()==200) {
				loadData();
				NotificationTemplate.success(delete.getMessage());
			}else {
				NotificationTemplate.error(delete.getMessage());
			}
		} catch (Exception e) {
			NotificationTemplate.error("Không thể xóa tài khoản này");
		}
	}
	

	private ApiUserFilter getSearch() {
		ApiUserFilter apiUserFilter = new ApiUserFilter();

		apiUserFilter.setLimit(paginationForm.getLimit());
		apiUserFilter.setSkip(paginationForm.getSkip());
		apiUserFilter.setKeyword(txtSearch.getValue());
		apiUserFilter.setActive(cbActive.getValue());
		if(cmbProvider.getValue().getKey() != null) {
			apiUserFilter.setProvider(cmbProvider.getValue().getKey());
		}
		
		if(cmbOrgOfUser.getValue().getKey() != null) {
			apiUserFilter.setHasUsed(cmbOrgOfUser.getValue().getKey());
		}
		
		if(cmbReviewOrg.getValue() != null) {
			if(cmbReviewOrg.getValue().getKey() == null) {
				apiUserFilter.setHasFirstReview(null);
				apiUserFilter.setFirstReviewed(null);
			}else {
				if(cmbReviewOrg.getValue().getKey().equals("2")) {
					apiUserFilter.setFirstReviewed(String.valueOf(true));
				}
				
				if(cmbReviewOrg.getValue().getKey().equals("1")) {
					apiUserFilter.setHasFirstReview(String.valueOf(true));
				}
			}
		}
		
		if(cbMyOrg.getValue()) {
			apiUserFilter.setIncludeOrganizationId(SessionUtil.getOrgId());
		}

		return apiUserFilter;
	}

}
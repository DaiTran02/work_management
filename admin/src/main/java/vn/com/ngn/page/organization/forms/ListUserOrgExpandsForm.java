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
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.api.organization.ApiUserOrganizationExpandsModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.forms.details.DetailRoleOfUserForm;
import vn.com.ngn.page.organization.models.ListUserIDsModel;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;
import vn.com.ngn.page.organization.models.UserOrganizationExpandsModel;
import vn.com.ngn.page.user.form.EditUserForm;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class ListUserOrgExpandsForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private UserOrganizationExpandsModel userItem;
	private Grid<UserOrganizationExpandsModel> grid = new Grid<UserOrganizationExpandsModel>(UserOrganizationExpandsModel.class,false);
	private List<UserOrganizationExpandsModel> listModel = new ArrayList<UserOrganizationExpandsModel>();
	private ListDataProvider<UserOrganizationExpandsModel> listDataProvider = new ListDataProvider<UserOrganizationExpandsModel>(listModel);
	private List<RoleOrganizationExpandsModel> listRoles = new ArrayList<RoleOrganizationExpandsModel>();

	private List<UserOrganizationExpandsModel> listUserSelected = new ArrayList<UserOrganizationExpandsModel>();
	int countRole = 0;

	private String parentId;
	private boolean isViewInParrent; 
	public ListUserOrgExpandsForm(String parentId,boolean isViewInParrent) {
		this.parentId = parentId;
		this.isViewInParrent = isViewInParrent;
		buildLayout();
		configComponent();
		loadData(parentId);
		loadRole();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.setPadding(false);
		this.add(createGrid());
	}

	@Override
	public void configComponent() {
	}

	public void loadData(String id) {
		listModel = new ArrayList<UserOrganizationExpandsModel>();
		try {
			if(id != null) {
				ApiResultResponse<List<ApiUserOrganizationExpandsModel>> data = ApiOrganizationService.getListUserOrg(id);
				listModel = data.getResult().stream().map(UserOrganizationExpandsModel::new).collect(Collectors.toList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		listDataProvider = new ListDataProvider<UserOrganizationExpandsModel>(listModel);
		grid.setItems(listDataProvider);
	}

	private Component createGrid() {
		grid = new Grid<UserOrganizationExpandsModel>(UserOrganizationExpandsModel.class,false);

		grid.addComponentColumn(model->{
			return new Span(model.getMoreInfo() == null ? "Đang xử lý" : model.getMoreInfo().getUsername());
		}).setHeader("Tên đăng nhập");
		grid.addComponentColumn(model->{
			return new Span(model.getMoreInfo() == null ? "Đang xử lý" : model.getMoreInfo().getUsername());
		}).setHeader("Tên");
		grid.addComponentColumn(model->{
			return new Span(model.getMoreInfo() == null ? "Đang xử lý" : model.getMoreInfo().getUsername());
		}).setHeader("Email");
		grid.addColumn(UserOrganizationExpandsModel::getPositionName).setHeader("Chức vụ");
		grid.addComponentColumn(model->{
			if(model.isActive()) {
				return createStatusIcon("Available");
			}else {
				return createStatusIcon("NotAvailable");
			}
		}).setHeader("Tình trạng").setWidth("120px").setFlexGrow(0);


		grid.addComponentColumn(model->{
			countRole = 0;

			List<RoleOrganizationExpandsModel> listRoleOfUser = new ArrayList<RoleOrganizationExpandsModel>();

			listRoles.stream().filter(role->role.getUserIds().contains(model.getUserId()))
			.forEach(roleName->{
				listRoleOfUser.add(roleName);
				countRole ++;
			});
			ButtonTemplate btnRole = new ButtonTemplate("Vai trò ("+countRole+")");
			btnRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnRole.addClickListener(e->{
				openDialogPermission(model.getUserId(),listRoleOfUser);
			});


			return btnRole;
		}).setHeader("Vai trò").setWidth("80px").setFlexGrow(0);

		grid.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();

			List<RoleOrganizationExpandsModel> listRoleOfUser = new ArrayList<RoleOrganizationExpandsModel>();

			listRoles.stream().filter(role->role.getUserIds().contains(model.getUserId()))
			.forEach(roleName->{
				listRoleOfUser.add(roleName);
			});

			Button btnEditUser = new Button(FontAwesome.Solid.ADDRESS_CARD.create());
			btnEditUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEditUser.getStyle().setCursor("pointer");
			btnEditUser.setTooltipText("Cập nhật thông tin người dùng");
			btnEditUser.addClickListener(e->{
				openDialogEditUser(model.getUserId());
			});

			Button btnEdit = new Button(FontAwesome.Solid.EDIT.create());
			btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEdit.getStyle().setCursor("pointer");
			btnEdit.setTooltipText("Cập nhật người dùng trong đơn vị");
			btnEdit.addClickListener(e->{
				openDialogEditUserInOrg(model.getUserId(),listRoleOfUser);
			});

			Button btnDelete = new Button(FontAwesome.Solid.REMOVE.create());
			btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnDelete.setTooltipText("Xóa người dùng khỏi đơn vị");
			btnDelete.getStyle().setCursor("pointer");
			btnDelete.addClickListener(e->{
				openConfirmDelete(parentId,model.getUserId());
			});

			hLayout.add(btnEditUser,btnEdit,btnDelete);

			return hLayout;
		}).setHeader("Thao tác").setWidth("150px").setFlexGrow(0);

		grid.addSelectionListener(e->{
			listUserSelected.clear();
			listUserSelected.addAll(e.getAllSelectedItems());
		});


		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		grid.setRowsDraggable(true);
		if(isViewInParrent) {
			grid.setHeight("200px");
		}
		grid.addDragStartListener(
				event -> {
					userItem = event.getDraggedItems().get(0);
					grid.setDropMode(GridDropMode.BETWEEN);
				}
				);
		grid.addDragEndListener(
				event -> {
					userItem = null;
					grid.setDropMode(null);
				}
				);

		grid.addDropListener(
				event -> {
					UserOrganizationExpandsModel dropOverItem = event.getDropTargetItem().get();
					if (!dropOverItem.equals(userItem)) {
						listModel.remove(userItem);
						int dropIndex =
								listModel.indexOf(dropOverItem) + (event.getDropLocation() == GridDropLocation.BELOW ? 1 : 0);
						listModel.add(dropIndex, userItem);
						grid.getDataProvider().refreshAll();
					}
					fireEvent(new ClickEvent(this,false));
				});

		return grid;
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

	private void loadRole() {
		listRoles = new ArrayList<RoleOrganizationExpandsModel>();
		try {
			if(parentId != null) {
				ApiResultResponse<List<ApiRoleOrganizationExpandsModel>> getListRule = ApiOrganizationService.getListRole(parentId);
				listRoles = getListRule.getResult().stream().map(RoleOrganizationExpandsModel::new).collect(Collectors.toList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openDialogEditUser(String userId) {
		DialogTemplate dialog = new DialogTemplate("CẬP NHẬT THÔNG TIN NGƯỜI DÙNG",()->{
		});

		EditUserForm editUser = new EditUserForm(userId,false,null, ()->{
			loadData(parentId);
			NotificationTemplate.success("Thành công");
			dialog.close();
		});

		dialog.getBtnSave().addClickListener(e->{
			editUser.doSave();
		});
		dialog.add(editUser);
		dialog.setWidth("40%");
		dialog.open();
	}


	private void openDialogEditUserInOrg(String userId,List<RoleOrganizationExpandsModel> listRoleOfUser) {
		DialogTemplate dialog = new DialogTemplate("CẬP NHẬT THÔNG TIN NGƯỜI DÙNG TRONG ĐƠN VỊ",()->{
		});

		System.out.println("Parend: "+parentId + "User ID"+userId);

		EditUserInOrgForm editUserInOrgForm = new EditUserInOrgForm(parentId,userId,listRoleOfUser,()->{
			loadData(parentId);
			dialog.close();
		});

		dialog.getBtnSave().addClickListener(e->{
			editUserInOrgForm.updateUser();
		});
		dialog.add(editUserInOrgForm);
		dialog.setWidth("40%");
		dialog.open();
	}

	private void openConfirmDelete(String parentId,String userId) {
		ConfirmDialogTemplate confirm = new ConfirmDialogTemplate("Xóa người dùng khỏi tổ chức");
		confirm.setText("Xác nhận xóa người dùng khỏi tổ chức");
		ListUserIDsModel listUserIDsModel = new ListUserIDsModel();
		listUserIDsModel.getUserIds().add(userId);
		confirm.addConfirmListener(e->{
			doDelete(parentId, listUserIDsModel);
		});
		confirm.setConfirmButtonTheme("error");
		confirm.setCancelable(true);
		confirm.open();
	}

	private void openDialogPermission(String idUser,List<RoleOrganizationExpandsModel> listRoleOfUser) {
		DialogTemplate dialog = new DialogTemplate("Danh sách vai trò của người dùng",()->{
		});

		DetailRoleOfUserForm detailRoleOfUserForm = new DetailRoleOfUserForm(parentId,idUser,listRoleOfUser);
		dialog.add(detailRoleOfUserForm);
		detailRoleOfUserForm.addChangeListener(e->{
			loadData(parentId);
			fireEvent(new ClickEvent(this,false));
		});

		dialog.getBtnSave().setVisible(false);
		dialog.setSizeFull();
		dialog.open();
	}

	private void doDelete(String parentId,ListUserIDsModel listUserIDsModel) {
		try {
			ApiResultResponse<Object> delete = ApiOrganizationService.deleteUserOfOrg(parentId, listUserIDsModel);
			if(delete.getStatus()==200) {
				loadData(parentId);
				NotificationTemplate.success("Xóa thành công");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<UserOrganizationExpandsModel> getListUser(){
		return listModel;
	}

}

























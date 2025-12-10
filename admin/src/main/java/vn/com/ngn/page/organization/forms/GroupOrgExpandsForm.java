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
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.forms.details.DetailChooseGroupFromOrgForm;
import vn.com.ngn.page.organization.models.GroupOrganizationExpandsModel;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;

public class GroupOrgExpandsForm extends VerticalLayout implements FormInterface {
	private static final long serialVersionUID = 1L;

	private Grid<GroupOrganizationExpandsModel> grid = new Grid<GroupOrganizationExpandsModel>(GroupOrganizationExpandsModel.class,false);
	private List<GroupOrganizationExpandsModel> listModel = new ArrayList<GroupOrganizationExpandsModel>();

	private ButtonTemplate btnAddGroup = new ButtonTemplate("Thêm tổ giao việc",FontAwesome.Solid.PLUS.create());
	private ButtonTemplate btnAddGroupFromOrg = new ButtonTemplate("Chọn tổ giao việc sẵn có",FontAwesome.Solid.HAND_POINTER.create());
	private ButtonTemplate btnSearch = new ButtonTemplate(FontAwesome.Solid.SEARCH.create());
	private TextField txtSeach = new TextField();
	private int countListGroup = 0;

	private String parentId;
	private Runnable onRun;
	public GroupOrgExpandsForm(String parentId,Runnable onRun) {
		this.onRun = onRun;
		this.parentId = parentId;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createToolbar(),createGrid());
	}

	@Override
	public void configComponent() {
		btnAddGroup.addClickListener(e->{
			openDialogCreateGroup();
		});
		
		btnAddGroupFromOrg.addClickListener(e->openDialogChooseOrg());
	}
	
	private Component createToolbar() {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		btnAddGroup.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		btnAddGroupFromOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		hLayout.setWidthFull();
		hLayout.add(btnAddGroup,btnAddGroupFromOrg);
		
		return hLayout;
	}

	public void loadData() {
		listModel = new ArrayList<GroupOrganizationExpandsModel>();
		try {
			ApiResultResponse<List<ApiGroupOrganizationExpandsModel>> listData = ApiOrganizationService.getListGroup(parentId);
			listModel = listData.getResult().stream().map(GroupOrganizationExpandsModel::new).collect(Collectors.toList());
			countListGroup = listData.getResult().size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		grid.setItems(listModel);
	}

	private Component createGrid() {
		grid = new Grid<GroupOrganizationExpandsModel>(GroupOrganizationExpandsModel.class,false);

		grid.addColumn(GroupOrganizationExpandsModel::getOrder).setHeader("STT").setWidth("50px").setFlexGrow(0);
		grid.addColumn(GroupOrganizationExpandsModel::getName).setHeader("Tên");
		grid.addColumn(GroupOrganizationExpandsModel::getDescription).setHeader("Mô tả");
		grid.addComponentColumn(model->{
			HorizontalLayout layout = new HorizontalLayout();

			Button btnShowUser = new Button("Người dùng ("+model.getUserIds().size()+")");
			btnShowUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnShowUser.getStyle().setCursor("pointer");
			btnShowUser.setWidth("130px");
			btnShowUser.addClickListener(e->{
				openDialogControlUser(parentId, model.getGroupId());
			});

			Button btnEdit = new Button(FontAwesome.Solid.EDIT.create());
			btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEdit.getStyle().setCursor("pointer");
			btnEdit.addClickListener(e->{
				openDialogUpdateGroup(model.getGroupId());
			});

			Button btnDelete = new Button(FontAwesome.Solid.TRASH.create());
			btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnDelete.getStyle().setCursor("pointer");
			btnDelete.addClickListener(e->{
				openConfirmDelete(model.getGroupId());
			});

			layout.add(btnShowUser,btnEdit,btnDelete);

			return layout;
		}).setHeader("Thao tác").setWidth("250px").setFlexGrow(0);

		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

		return grid;
	}

	@SuppressWarnings("unused")
	private Component createToobar() {
		HorizontalLayout hLayout = new HorizontalLayout();

		txtSeach.setPlaceholder("Nhập từ khóa để lọc");

		btnSearch.getStyle().setCursor("pointer");
		btnSearch.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		btnAddGroup.getStyle().setCursor("pointer");
		btnAddGroup.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		hLayout.add(txtSeach,btnSearch,btnAddGroup);
		hLayout.expand(txtSeach);
		hLayout.setWidthFull();

		return hLayout;
	}


	private void openDialogCreateGroup() {
		DialogTemplate dialog = new DialogTemplate("THÊM TỔ GIAO VIỆC",()->{

		});

		EditGroupOrgExpandsForm editGroupOrgExpandsForm = new EditGroupOrgExpandsForm(parentId, null, countListGroup, ()->{
			NotificationTemplate.success("Thành công ");
			loadData();
			onRun.run();
			dialog.close();
		},listModel);

		dialog.add(editGroupOrgExpandsForm);
		dialog.getBtnSave().addClickListener(e->{
			editGroupOrgExpandsForm.doSave();
		});
		dialog.setWidth("60%");
		dialog.open();
	}

	private void openDialogUpdateGroup(String idGroup) {
		DialogTemplate dialog = new DialogTemplate("CHỈNH SỬA TỔ GIAO VIỆC",()->{

		});

		EditGroupOrgExpandsForm editGroupOrgExpandsForm = new EditGroupOrgExpandsForm(parentId, idGroup, countListGroup, ()->{
			NotificationTemplate.success("Thành công");
			loadData();
			dialog.close();
		},listModel);

		dialog.add(editGroupOrgExpandsForm);
		dialog.getBtnSave().addClickListener(e->{
			editGroupOrgExpandsForm.doSave();
		});
		dialog.setWidth("60%");
		dialog.open();
	}

	private void openDialogControlUser(String parentId,String groupId) {
		DialogTemplate dialog = new DialogTemplate("PHÂN NGƯỜI DÙNG",()->{
			loadData();
		});

		TabDevisionUserForGroupForm tabDevisionUserForGroupForm = new TabDevisionUserForGroupForm(parentId, groupId);
		dialog.add(tabDevisionUserForGroupForm);

		dialog.getBtnSave().addClickListener(e->{
			loadData();
			dialog.close();
		});
		dialog.setWidth("100%");
		dialog.setHeight("99%");
		dialog.open();
	}

	private void openDialogChooseOrg() {
		DialogTemplate dialogTemplate = new DialogTemplate("CHỌN NHÓM GIAO VIỆC", ()->{});
		
		DetailChooseGroupFromOrgForm detailChooseGroupFromOrgForm = new DetailChooseGroupFromOrgForm(parentId);
		dialogTemplate.add(detailChooseGroupFromOrgForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.open();
		dialogTemplate.getBtnSave().setText("Thêm vào tổ");
		dialogTemplate.getBtnSave().addClickListener(e->{
			detailChooseGroupFromOrgForm.createGroup();
		});
		detailChooseGroupFromOrgForm.addChangeListener(e->{
			loadData();
			NotificationTemplate.success("Thành công");
			dialogTemplate.close();
		});
	}

	private void openConfirmDelete(String idGroup) {
		ConfirmDialogTemplate confirm = new ConfirmDialogTemplate("Xóa tổ giao việc");
		confirm.setText("Xác nhận xóa tổ giao việc");
		confirm.addConfirmListener(e->{
			doDeleteGroup(idGroup);
		});
		confirm.setConfirmButtonTheme("error");
		confirm.setCancelable(true);
		confirm.open();
	}

	private void doDeleteGroup(String idGroup) {
		ApiResultResponse<Object> delete = null;
		try {
			delete = ApiOrganizationService.deleteGroup(parentId, idGroup);
			if(delete.getStatus()==200) {
				NotificationTemplate.success("Thành công");
				loadData();
			}else {
				NotificationTemplate.error(delete.getMessage());
			}
		} catch (Exception e) {
			NotificationTemplate.error("Không thể xóa vì tổ chức đang có người dùng");
			e.printStackTrace();
		}
	}

}

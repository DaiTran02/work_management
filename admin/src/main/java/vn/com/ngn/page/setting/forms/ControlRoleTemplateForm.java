package vn.com.ngn.page.setting.forms;

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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;
import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.PaginationForm;
import vn.com.ngn.views.MainLayout;

@Route(value = "role", layout = MainLayout.class)
@PageTitle("Vai trò mẫu")
@PermitAll
public class ControlRoleTemplateForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private Grid<RoleOrganizationExpandsModel> grid = new Grid<RoleOrganizationExpandsModel>(RoleOrganizationExpandsModel.class,false);
	private List<RoleOrganizationExpandsModel> listModel = new ArrayList<RoleOrganizationExpandsModel>();
	
	private TextField txtSearch = new TextField();
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());
	private Button btnAddNewRole = new Button("Thêm vai trò mẫu",FontAwesome.Solid.PLUS.create());
	
	private PaginationForm paginationForm;
	
	public ControlRoleTemplateForm() {
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		paginationForm = new PaginationForm(()->{
			if(paginationForm!=null) {
				loadData();
			}
		});
		
		this.add(createToolbar(),paginationForm,createGrid());
	}

	@Override
	public void configComponent() {
		txtSearch.addValueChangeListener(e->{
			loadData();
		});
		
		btnSearch.addClickListener(e->{
			loadData();
		});
		
		btnAddNewRole.addClickListener(e->{
			openDiaLogCreateOrg(null);
		});
	}
	
	public void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiRoleOrganizationExpandsModel>> listRole = ApiOrganizationService.getListRoleTemplate(paginationForm.getSkip(),paginationForm.getLimit(),txtSearch.getValue());
			paginationForm.setItemCount(listRole.getTotal());
			listModel = listRole.getResult().stream().map(RoleOrganizationExpandsModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		grid.setItems(listModel);
	}
	
	private Component createGrid() {
		grid = new Grid<RoleOrganizationExpandsModel>(RoleOrganizationExpandsModel.class,false);
		
		grid.addColumn(RoleOrganizationExpandsModel::getName).setHeader("Tên").setWidth("30%").setFlexGrow(0);
		grid.addColumn(RoleOrganizationExpandsModel::getDescription).setHeader("Mô tả");
		grid.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();
			
			Button btnPermisson = new Button("Phân quyền ("+model.getPermissionKeys().size()+")");
			btnPermisson.getStyle().setCursor("pointer");
			btnPermisson.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnPermisson.setWidth("120px");
			btnPermisson.addClickListener(e->{
				openDialogPermission(model.getName(),model.getId());
			});
			
			Button btnEdit = new Button(FontAwesome.Solid.EDIT.create());
			btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEdit.getStyle().setCursor("pointer");
			btnEdit.setTooltipText("Chỉnh sửa");
			btnEdit.addClickListener(e->{
				openDiaLogUpdateOrg(model.getId());
			});
			
			Button btnDelete = new Button(FontAwesome.Solid.TRASH.create());
			btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnDelete.getStyle().setCursor("pointer");
			btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnDelete.addClickListener(e->{
				openConfirmDelete(model.getId());
			});
			
			hLayout.add(btnPermisson,btnEdit,btnDelete);
			return hLayout;
		}).setWidth("220px").setFlexGrow(0);
		
		grid.setSizeFull();
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		
		return grid;
	}
	
	
	private Component createToolbar() {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		
		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Nhập để tìm...");

		btnSearch.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSearch.getStyle().setCursor("pointer");
		
		btnAddNewRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAddNewRole.getStyle().setCursor("pointer");
		
		hLayout.setWidthFull();
		hLayout.add(txtSearch,btnSearch,btnAddNewRole);
		hLayout.expand(txtSearch);
		
		return hLayout;
	}
	
	private void openDiaLogCreateOrg(String roleId) {
		DialogTemplate dialog = new DialogTemplate("THÊM VAI TRÒ MẪU",()->{

		});
		
		EditControlRoleTemplateForm editControlRoleTemplateForm = new EditControlRoleTemplateForm(roleId,()->{
			loadData();
			NotificationTemplate.success("Thêm thành công");
			dialog.close();
		});
		
		dialog.add(editControlRoleTemplateForm);
		dialog.setWidth("60%");
		dialog.getBtnSave().addClickListener(e->{
			editControlRoleTemplateForm.saveRole();
		});
		dialog.open();
	}
	
	private void openDiaLogUpdateOrg(String roleId) {
		DialogTemplate dialog = new DialogTemplate("CHỈNH SỬA VAI TRÒ MẪU",()->{

		});
		
		EditControlRoleTemplateForm editControlRoleTemplateForm = new EditControlRoleTemplateForm(roleId,()->{
			loadData();
			NotificationTemplate.success("Chỉnh sửa thành công");
			dialog.close();
		});
		
		dialog.add(editControlRoleTemplateForm);
		dialog.setWidth("60%");
		dialog.getBtnSave().addClickListener(e->{
			editControlRoleTemplateForm.saveRole();
		});
		dialog.open();
	}
	
	private void openDialogPermission(String name,String roleId) {
		DialogTemplate dialog = new DialogTemplate("PHÂN QUYỀN - "+name,()->{
			loadData();
		});
		
		PermissionTemplateForm permissionTemplateForm = new PermissionTemplateForm(roleId, ()->{
			loadData();
			NotificationTemplate.success("Phân quyền thành công");
			dialog.close();
		});
		
		dialog.add(permissionTemplateForm);
		
		dialog.getBtnSave().addClickListener(e->{
			permissionTemplateForm.doPermission();
		});
		dialog.setWidth("100%");
		dialog.setHeight("99%");
		dialog.open();
	}
	
	private void openConfirmDelete(String roleId) {
		ConfirmDialogTemplate confirm = new ConfirmDialogTemplate("Xóa vai trò mẫu");
		confirm.setText("Xác nhận xóa vai trò");
		confirm.addConfirmListener(e->{
			doDelete(roleId);
			confirm.close();
		});
		confirm.setConfirmButtonTheme("error");
		confirm.setCancelable(true);
		confirm.open();
	}

	private void doDelete(String roleId) {
		try {
			ApiResultResponse<Object> deleteRole = ApiOrganizationService.deleteRoleTemplate(roleId);
			if(deleteRole.getStatus() == 200) {
				loadData();
				NotificationTemplate.success("Xóa thành công");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}












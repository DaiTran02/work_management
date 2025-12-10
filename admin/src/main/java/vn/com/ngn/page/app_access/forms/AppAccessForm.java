package vn.com.ngn.page.app_access.forms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.app_access.ApiAppAccessModel;
import vn.com.ngn.api.app_access.ApiAppAccessService;
import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.app_access.models.AppAccessModel;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.PaginationForm;

public class AppAccessForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY");
	private PaginationForm paginationForm;

	private Grid<AppAccessModel> grid = new Grid<AppAccessModel>(AppAccessModel.class,false);
	private List<AppAccessModel> listModel = new ArrayList<AppAccessModel>();

	private TextField txtSearch = new TextField();
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());
	private Button btnAddNewAccess = new Button("Thêm truy cập mới",FontAwesome.Solid.PLUS.create());

	private ComboBox<Pair<String, String>> cmbActive = new ComboBox<Pair<String,String>>();
	private List<Pair<String, String>> listDataCmbActive = new ArrayList<Pair<String,String>>();

	public AppAccessForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();

		paginationForm = new PaginationForm(()->{
			if(paginationForm != null) {
				loadData();
			}
		});

		this.add(createToolbar(),paginationForm,createGrid());

	}

	@Override
	public void configComponent() {
		loadDataCmb();
		cmbActive.addValueChangeListener(e->{
			loadData();
		});

		btnAddNewAccess.addClickListener(e->{
			openDiaLogCreateAppAccess();
		});

		txtSearch.addValueChangeListener(e->{
			loadData();
		});

		btnSearch.addClickListener(e->{
			loadData();
		});

		btnSearch.addClickShortcut(Key.ENTER);

	}

	public void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiAppAccessModel>> getListData = ApiAppAccessService.getListAppAccess(paginationForm.getSkip(), paginationForm.getLimit(),cmbActive.getValue().getKey(),txtSearch.getValue());
			paginationForm.setItemCount(getListData.getTotal());
			listModel = getListData.getResult().stream().map(AppAccessModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		grid.setItems(listModel);

	}

	private Component createGrid() {
		grid = new Grid<AppAccessModel>(AppAccessModel.class,false);

		grid.addComponentColumn(model->{
			if(model.isActive()) {
				return createStatusIcon("Available");
			}else {
				return createStatusIcon("no");
			}
		}).setWidth("40px").setFlexGrow(0);
		grid.addColumn(AppAccessModel::getName).setHeader("Tên").setResizable(true);
		grid.addColumn(AppAccessModel::getDescription).setHeader("Mô tả").setResizable(true);
		grid.addComponentColumn(model->{
			return new Span(df.format(model.getCreatedTime()));
		}).setHeader("Ngày tạo");
		grid.addColumn(AppAccessModel::getCreatorName).setHeader("Người tạo");

		grid.addComponentColumn(model->{
			return new Span(df.format(model.getStartTime()));
		}).setHeader("Ngày bắt đầu").setWidth("120px").setFlexGrow(0);

		grid.addComponentColumn(model->{
			return new Span(df.format(model.getEndTime()));
		}).setHeader("Ngày kết thúc").setWidth("120px").setFlexGrow(0);

		grid.addComponentColumn(model->{
			Button btnCoppy = new Button(FontAwesome.Solid.COPY.create());
			btnCoppy.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnCoppy.getStyle().setCursor("pointer");


			PasswordField passwordField = new PasswordField();
			passwordField.setValue(model.getApiKey());
			passwordField.setSizeFull();

			passwordField.setClassName("token-password");
			
			return passwordField;
		}).setHeader("Token");
		
		grid.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();

			Button btnEdit = new Button(FontAwesome.Solid.EDIT.create());
			btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEdit.getStyle().setCursor("pointer");
			btnEdit.addClickListener(e->{
				openDiaLogUpdateAppAccess(model.getId());
			});

			Button btnDelete = new Button(FontAwesome.Solid.TRASH.create());
			btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnDelete.getStyle().setCursor("pointer");
			btnDelete.addClickListener(e->{
				openConfirmDelete(model.getId());
			});

			hLayout.add(btnEdit,btnDelete);
			return hLayout;
		}).setHeader("Thao tác").setWidth("100px").setFlexGrow(0);

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
		HorizontalLayout hLayout = new HorizontalLayout();

		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Nhập từ khóa để tìm...");

		cmbActive.setWidth("250px");

		btnSearch.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSearch.getStyle().setCursor("pointer");

		btnAddNewAccess.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAddNewAccess.getStyle().setCursor("pointer");

		hLayout.add(txtSearch,cmbActive,btnSearch,btnAddNewAccess);
		hLayout.expand(txtSearch);
		hLayout.setWidthFull();

		return hLayout;
	}
	
	private Button createStatusIcon(String status) {
		boolean isAvailable = "Available".equals(status);
		Button btnStatus;
		if (isAvailable) {
			btnStatus = new Button(FontAwesome.Solid.CHECK.create());
			btnStatus.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
			btnStatus.setTooltipText("Hoạt động");
		} else {
			btnStatus = new Button(FontAwesome.Solid.CLOSE.create());
			btnStatus.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnStatus.setTooltipText("Không hoạt động");
		}
		btnStatus.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		return btnStatus;
	}

	private void loadDataCmb() {
		listDataCmbActive.clear();

		Pair<String, String> item = Pair.of(null,"Tất cả");
		listDataCmbActive.add(item);
		Pair<String, String> item2 = Pair.of("true","Hoạt động");
		listDataCmbActive.add(item2);
		Pair<String, String> item3 = Pair.of("false","Không hoạt động");
		listDataCmbActive.add(item3);

		cmbActive.setItems(listDataCmbActive);
		cmbActive.setItemLabelGenerator(Pair::getRight);
		cmbActive.setValue(listDataCmbActive.get(0));

	}

	private void openDiaLogCreateAppAccess() {
		DialogTemplate dialog = new DialogTemplate("THÊM TRUY CẬP MỚI",()->{

		});
		EditAppAccessForm editAppAccessForm = new EditAppAccessForm(null,()->{
			loadData();
			dialog.close();
		});
		dialog.add(editAppAccessForm);
		dialog.setWidth("60%");
		dialog.getBtnSave().addClickListener(e->{
			editAppAccessForm.save();
		});
		dialog.open();
	}

	private void openDiaLogUpdateAppAccess(String accessId) {
		DialogTemplate dialog = new DialogTemplate("CẬP NHẬT",()->{

		});
		EditAppAccessForm editAppAccessForm = new EditAppAccessForm(accessId,()->{
			loadData();
			dialog.close();
		});
		dialog.add(editAppAccessForm);
		dialog.setWidth("60%");
		dialog.getBtnSave().addClickListener(e->{
			editAppAccessForm.save();
		});
		dialog.open();
	}

	private void openConfirmDelete(String accessId) {
		ConfirmDialogTemplate confirm = new ConfirmDialogTemplate("Xóa truy cập");
		confirm.setText("Xác nhận xóa ");
		confirm.addConfirmListener(e->{
			doDelete(accessId);
		});
		confirm.setConfirmButtonTheme("error");
		confirm.setCancelable(true);
		confirm.open();
	}

	private void doDelete(String accessId) {
		try {
			ApiResultResponse<Object> delete = ApiAppAccessService.deleteAppAccess(accessId);
			if(delete.getStatus() == 200 || delete.getStatus() == 201) {
				NotificationTemplate.success("Thành công");
				loadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
























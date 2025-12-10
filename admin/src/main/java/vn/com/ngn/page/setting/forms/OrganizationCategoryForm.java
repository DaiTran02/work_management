package vn.com.ngn.page.setting.forms;

import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryFilterModel;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryModel;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.PaginationForm;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class OrganizationCategoryForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private ButtonTemplate btnCreateOrgCategory = new ButtonTemplate("Thêm loại đơn vị mới",FontAwesome.Solid.PLUS.create());
	private TextField txtSearch = new TextField();
	private PaginationForm paginationForm;
	private Checkbox cbActive = new Checkbox("Hoạt động");
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm",FontAwesome.Solid.SEARCH.create());

	private Grid<ApiOrganizationCategoryModel> grid = new Grid<ApiOrganizationCategoryModel>();
	private List<ApiOrganizationCategoryModel> listModel = new ArrayList<ApiOrganizationCategoryModel>();


	public OrganizationCategoryForm() {
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

		this.add(createLayout(),paginationForm,createGrid());
	}

	@Override
	public void configComponent() {
		btnSearch.addClickListener(e->{
			loadData();
		});

		btnCreateOrgCategory.addClickListener(e->{
			openDialogCreateOrgcate();
		});
		
		cbActive.addClickListener(e->loadData());

	}

	public void loadData() {
		listModel = new ArrayList<ApiOrganizationCategoryModel>();
		try {
			ApiResultResponse<List<ApiOrganizationCategoryModel>> listApiResultResponse = ApiOrganizationCategoryService.getListOrganizationCategory(getFilter());
			paginationForm.setItemCount(listApiResultResponse.getTotal());
			listModel = listApiResultResponse.getResult();
		} catch (Exception e) {
			e.printStackTrace();
		}

		grid.setItems(listModel);
		fireEvent(new ClickEvent(this,false));
	}

	private Component createGrid() {
		grid = new Grid<ApiOrganizationCategoryModel>(ApiOrganizationCategoryModel.class,false);

		grid.addColumn(ApiOrganizationCategoryModel::getOrder).setHeader("STT").setWidth("60px").setFlexGrow(0);
		grid.addColumn(ApiOrganizationCategoryModel::getName).setHeader("Loại đơn vị");
		grid.addColumn(ApiOrganizationCategoryModel::getDescription).setHeader("Mô tả");
		grid.addColumn(ApiOrganizationCategoryModel::getCount).setHeader("Đơn vị sử dụng");
		grid.addComponentColumn(model->{
			Span span;
			if(model.isActive()) {
				span = new Span("Sử dụng");
				span.getElement().getThemeList().add("badge success");
			}else {
				span = new Span("Không sử dụng");
				span.getElement().getThemeList().add("badge error");
			}
			return span;
		}).setHeader("Trạng thái");
		
		grid.addComponentColumn(model->{
			HorizontalLayout horizontalLayout = new HorizontalLayout();

			ButtonTemplate btnEdit = new ButtonTemplate(FontAwesome.Solid.EDIT.create());
			btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEdit.addClickListener(e->{
				openDialogUpdateOrgcate(model.getId());
			});

			ButtonTemplate btnDelete = new ButtonTemplate(FontAwesome.Solid.TRASH.create());
			btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);

			horizontalLayout.add(btnEdit,btnDelete);

			return horizontalLayout;
		}).setWidth("200px").setFlexGrow(0);
		
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);


		return grid;
	}

	private Component createLayout() {
		HorizontalLayout hLayout = new HorizontalLayout();

		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Nhập từ khóa để tìm...");

		cbActive.setWidth("170px");
		cbActive.getStyle().set("margin-top", "10px");
		cbActive.setValue(true);

		hLayout.add(txtSearch,cbActive,btnSearch,btnCreateOrgCategory);
		hLayout.expand(txtSearch);
		hLayout.setWidthFull();
		return hLayout;
	}

	private ApiOrganizationCategoryFilterModel getFilter() {
		ApiOrganizationCategoryFilterModel apiOrganizationCategoryFilterModel = new ApiOrganizationCategoryFilterModel();
		apiOrganizationCategoryFilterModel.setSkip(paginationForm.getSkip());
		apiOrganizationCategoryFilterModel.setLimit(paginationForm.getLimit());
		apiOrganizationCategoryFilterModel.setKeyword(txtSearch.getValue());
		apiOrganizationCategoryFilterModel.setActive(cbActive.getValue());
		return apiOrganizationCategoryFilterModel;
	}

	private void openDialogCreateOrgcate() {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm loại đơn vị mới",()->{

		});

		EditOrganizationCategoryForm editOrganizationCategoryForm = new EditOrganizationCategoryForm(null,listModel.size());
		editOrganizationCategoryForm.addChangeListener(e->{
			loadData();
			dialogTemplate.close();
		});
		dialogTemplate.add(editOrganizationCategoryForm);
		dialogTemplate.getBtnSave().addClickListener(e->{
			editOrganizationCategoryForm.saveOrgCate();
		});
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeight("60%");

		dialogTemplate.open();
	}

	private void openDialogUpdateOrgcate(String idOrg) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chỉnh sửa loại đơn vị",()->{

		});

		EditOrganizationCategoryForm editOrganizationCategoryForm = new EditOrganizationCategoryForm(idOrg,0);
		editOrganizationCategoryForm.addChangeListener(e->{
			loadData();
			dialogTemplate.close();
		});
		dialogTemplate.add(editOrganizationCategoryForm);
		dialogTemplate.getBtnSave().addClickListener(e->{
			editOrganizationCategoryForm.saveOrgCate();
		});
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeight("60%");

		dialogTemplate.open();
	}


}

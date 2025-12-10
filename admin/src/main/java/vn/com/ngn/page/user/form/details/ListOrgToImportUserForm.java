package vn.com.ngn.page.user.form.details;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.OrganizationModel;
import vn.com.ngn.utils.CheckPermissionUtil;
import vn.com.ngn.utils.SessionUtil;
import vn.com.ngn.utils.components.OrganizationNavForm;
import vn.com.ngn.utils.components.PaginationForm;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class ListOrgToImportUserForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout navLayout = new VerticalLayout();
	private HorizontalLayout hFolderLayout = new HorizontalLayout();
	private VerticalLayout contentLayout = new VerticalLayout();

	private CheckPermissionUtil checkPermissionUtil = new CheckPermissionUtil();
	private OrganizationNavForm organizationNavForm = new OrganizationNavForm();
	private PaginationForm paginationForm;

	private Grid<OrganizationModel> grid = new Grid<OrganizationModel>(OrganizationModel.class,false);
	private List<OrganizationModel> listModel = new ArrayList<OrganizationModel>();
	
	private ApiOrganizationModel infoParent = new ApiOrganizationModel();

	private TextField txtSearch = new TextField();
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());

	private Button btnBackToOldOrg = new Button(FontAwesome.Solid.ARROW_LEFT.create());
	private H5 titleOrg = new H5();

	private OrganizationModel organizationModel;

	private String thisParentId = null;

	private String orgId;
	private Runnable onRun;
	public ListOrgToImportUserForm(String orgId,Runnable onRun) {
		this.orgId = orgId;
		this.onRun = onRun;
		Pair<String, String> item =  Pair.of(null,"Các tổ chức cấp cao nhất");
		if(checkPermissionUtil.checkAdmin()) {
			item = Pair.of(null,"Các tổ chức cấp cao nhất");
		}else {
			ApiOrganizationModel apiOrganizationModel = getInfoOrg(SessionUtil.getOrgId());
			if(apiOrganizationModel != null) {
				if(apiOrganizationModel.getParentId() != null) {
					item = Pair.of(apiOrganizationModel.getParentId().toString(),"Đơn vị đang sử dụng");
					thisParentId = apiOrganizationModel.getParentId().toString();
					infoParent = apiOrganizationModel;
				}else {
					item = Pair.of(null,"Các tổ chức cấp cao nhất");
				}
			}
		}
		organizationNavForm.addItem(item);

		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		titleOrg.getStyle().set("margin-top", "11px");

		btnBackToOldOrg.getStyle().setCursor("pointer");
		btnBackToOldOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		hFolderLayout.getStyle().set("border-bottom", "1px solid black");
		hFolderLayout.setWidthFull();
		hFolderLayout.expand(titleOrg);

		paginationForm = new PaginationForm(()->{
			if(paginationForm != null) {
				loadData();
			}
		});

		if(organizationNavForm.getCurrentItem().getLeft() == null) {
			titleOrg.removeAll();
			titleOrg.add("Danh sách tổ chức");
			hFolderLayout.add(titleOrg);
			contentLayout.add(paginationForm,createGrid());
		}else {
			titleOrg.removeAll();
			titleOrg.add("Danh sách tổ chức");
			hFolderLayout.add(titleOrg);
			contentLayout.add(paginationForm,createGrid());
		}
		

		navLayout.add(organizationNavForm,hFolderLayout,createToolbar());
		navLayout.setPadding(false);

		this.add(navLayout,contentLayout);

	}

	@Override
	public void configComponent() {
		organizationNavForm.addClickListener(e->{
			Pair<String, String> itemNav = organizationNavForm.getCurrentItem();
			this.thisParentId = itemNav.getLeft();
			loadData();

			if(itemNav.getLeft() == null) {
				hFolderLayout.removeAll();
				titleOrg.removeAll();
				titleOrg.add("Danh sách tổ chức");

				hFolderLayout.add(titleOrg);
			}else {
				hFolderLayout.removeAll();
				titleOrg.removeAll();
				titleOrg.add(itemNav.getRight());
				btnBackToOldOrg.setTooltipText("Quay lại");
				hFolderLayout.add(btnBackToOldOrg,titleOrg);
			}
			if(thisParentId.equals(infoParent.getId())) {
				btnBackToOldOrg.setVisible(false);
			}else {
				btnBackToOldOrg.setVisible(true);
			}
		});

		btnBackToOldOrg.addClickListener(e->{
			Pair<String, String> itemNav = organizationNavForm.getItem();

			organizationNavForm.removeItem(itemNav);
			organizationNavForm.buildLayout();
			this.thisParentId = itemNav.getLeft();
			loadData();

			if(itemNav.getLeft() == null) {
				hFolderLayout.removeAll();

				titleOrg.removeAll();
				titleOrg.add("Danh sách tổ chức");

				hFolderLayout.add(titleOrg);
			}else {
				hFolderLayout.removeAll();

				titleOrg.removeAll();
				titleOrg.add(itemNav.getRight());

				btnBackToOldOrg.setTooltipText("Quay lại ");
				hFolderLayout.add(btnBackToOldOrg,titleOrg);
			}
			if(thisParentId.equals(infoParent.getId())) {
				btnBackToOldOrg.setVisible(false);
			}else {
				btnBackToOldOrg.setVisible(true);
			}
		});

		txtSearch.addValueChangeListener(e->{
			loadData();
		});

		btnSearch.addClickListener(e->{
			loadData();
		});


	}

	public void loadData() {
		listModel = new ArrayList<OrganizationModel>();

		try {
			ApiResultResponse<List<ApiOrganizationModel>> listDataOrg = ApiOrganizationService.getListOrganization(paginationForm.getSkip(), paginationForm.getLimit(), thisParentId, txtSearch.getValue(),null);
			listModel = listDataOrg.getResult().stream().map(OrganizationModel::new).collect(Collectors.toList());
			
			System.out.println("parentId: "+thisParentId + "Infoparent: "+ infoParent.getId());
			if(thisParentId.equals(infoParent.getParentId())) {
				System.out.println("Tesst");
				listModel.removeIf(model->{
					return !model.getId().equals(SessionUtil.getOrgId());
				});
			}
			paginationForm.setItemCount(listDataOrg.getTotal());
		} catch (Exception e) {
			e.printStackTrace();
		}

		grid.setItems(listModel);
	}
	

	private Grid<OrganizationModel> createGrid() {
		grid = new Grid<OrganizationModel>(OrganizationModel.class,false);

		grid.addComponentColumn(model->{
			Button btnOrg = new Button(model.getName());

			btnOrg.getStyle().setCursor("pointer");
			btnOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			
			btnOrg.addClickListener(e->{
				Pair<String, String> item = Pair.of(model.getId(),model.getName());
				organizationNavForm.addItem(item);
				
				this.thisParentId = model.getId();
				loadData();
				
				titleOrg.removeAll();
				titleOrg.add(model.getName());
				
				hFolderLayout.removeAll();
				hFolderLayout.add(btnBackToOldOrg,titleOrg);
				btnBackToOldOrg.setTooltipText("Quay lại ");
				if(thisParentId.equals(infoParent.getId())) {
					btnBackToOldOrg.setVisible(false);
				}else {
					btnBackToOldOrg.setVisible(true);
				}
			});
			
			String text = String.valueOf(model.getCountSubOrganization()+" Đơn vị trực thuộc và "+ model.getUserOrganizationExpands().size()+ " Người dùng");

			Span span = new Span(text);
			span.getStyle().set("font-size", "12px");
			span.getStyle().set("margin-left", "6px");
			span.getStyle().set("font-style", "italic");
			
			VerticalLayout vlayout = new VerticalLayout();
			vlayout.setSpacing(false);
			vlayout.setPadding(false);
			vlayout.add(btnOrg,span);

			return vlayout;
		}).setHeader("Tên đơn vị");
		grid.addColumn(OrganizationModel::getDescription).setHeader("Mô tả");
		grid.addComponentColumn(model->{
			Button btnChoose = new Button("Chọn");

			btnChoose.getStyle().setCursor("pointer");
			btnChoose.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnChoose.addClickListener(e->{
				setOrg(model);
				onRun.run();
			});
			
			if(model.getId().equals(orgId)) {
				btnChoose.setEnabled(false);
			}

			return btnChoose;
		}).setHeader("Thao tác").setWidth("100px").setFlexGrow(0);

		grid.setWidthFull();
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

		return grid;
	}



	private Component createToolbar() {
		HorizontalLayout horizontalLayout = new HorizontalLayout();

		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Nhập từ khóa để tìm kiếm");
		txtSearch.setClearButtonVisible(true);

		btnSearch.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSearch.getStyle().setCursor("pointer");
		btnSearch.addClickShortcut(Key.ENTER);

		horizontalLayout.add(txtSearch,btnSearch);
		horizontalLayout.expand(txtSearch);
		horizontalLayout.setWidthFull();


		return horizontalLayout;
	}
	
	private ApiOrganizationModel getInfoOrg(String idOrg) {
		try {
			ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(idOrg);
			if(data.isSuscces()) {
				return data.getResult();
			}
		} catch (Exception e) {
			
		}
		
		return null;
		
	}
	
	public void setOrg(OrganizationModel organizationModel) {
		this.organizationModel = organizationModel;
	}
	
	public OrganizationModel getOrg() {
		return this.organizationModel;
	}


}

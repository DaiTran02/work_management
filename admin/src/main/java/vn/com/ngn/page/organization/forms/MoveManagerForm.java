package vn.com.ngn.page.organization.forms;

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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiCreateAndUpdateOrgModel;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.CreateAndUpdateOrgModel;
import vn.com.ngn.page.organization.models.OrganizationModel;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.OrganizationNavForm;
import vn.com.ngn.utils.components.PaginationForm;

public class MoveManagerForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean checkChooseManagert = false;
	
	private VerticalLayout navLayout = new VerticalLayout();
	private HorizontalLayout hFolderLayout = new HorizontalLayout();
	private VerticalLayout contentLayout = new VerticalLayout();

	private OrganizationNavForm organizationNavForm = new OrganizationNavForm();
	private PaginationForm paginationForm;

	private Grid<OrganizationModel> grid = new Grid<OrganizationModel>(OrganizationModel.class,false);
	private List<OrganizationModel> listModel = new ArrayList<OrganizationModel>();

	private TextField txtSearch = new TextField();
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());

	private Button btnBackToOldOrg = new Button(FontAwesome.Solid.ARROW_LEFT.create());
	private H5 titleOrg = new H5();

	private Button btnChooseManagert = new Button("Chuyển đơn vị lên các cấp cao nhất",FontAwesome.Solid.HOME.create());

	private String thisParentId = null;

	private String orgId;
	private String parentId;
	public MoveManagerForm(String orgId,String parentId) {
		this.orgId = orgId;
		this.parentId = parentId;
		Pair<String, String> item =  Pair.of(null,"Các tổ chức cấp cao nhất");
		organizationNavForm.addItem(item);

		buildLayout();
		configComponent();
		loadData();
		getDataThisOrg();
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
		}
		
		btnChooseManagert.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnChooseManagert.getStyle().setCursor("pointer");

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
				btnBackToOldOrg.setTooltipText("Quay lại "+organizationNavForm.getItem().getRight());
				hFolderLayout.add(btnBackToOldOrg,titleOrg);
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

				btnBackToOldOrg.setTooltipText("Quay lại "+organizationNavForm.getItem().getRight());

				hFolderLayout.add(btnBackToOldOrg,titleOrg);
			}
		});

		txtSearch.addValueChangeListener(e->{
			loadData();
		});

		btnSearch.addClickListener(e->{
			loadData();
		});

		btnChooseManagert.addClickListener(e->{
			openConfirmMoveOrgToManagert();
		});

	}

	public void loadData() {
		listModel.clear();

		try {
			ApiResultResponse<List<ApiOrganizationModel>> listDataOrg = ApiOrganizationService.getListOrganization(paginationForm.getSkip(), paginationForm.getLimit(), thisParentId, txtSearch.getValue(),null);
			listModel = listDataOrg.getResult().stream().map(OrganizationModel::new).collect(Collectors.toList());
			paginationForm.setItemCount(listDataOrg.getTotal());
		} catch (Exception e) {
			e.printStackTrace();
		}

		grid.setItems(listModel);
		if(parentId == null) {
			btnChooseManagert.setEnabled(false);
		}else {
			btnChooseManagert.setEnabled(true);
		}
	}
	
	private void getDataThisOrg() {
		try {
			ApiResultResponse<ApiOrganizationModel> getOrg = ApiOrganizationService.getOneOrg(orgId);
			if(getOrg.getResult().getParentId() == null) {
				btnChooseManagert.setEnabled(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				
				hFolderLayout.removeAll();
				hFolderLayout.add(btnBackToOldOrg,titleOrg);
				btnBackToOldOrg.setTooltipText("Quay lại "+organizationNavForm.getItem().getRight());
			});

			return btnOrg;
		}).setHeader("Tên đơn vị");
		grid.addColumn(OrganizationModel::getDescription).setHeader("Mô tả");
		grid.addComponentColumn(model->{
			Button btnChoose = new Button("Chọn");

			btnChoose.getStyle().setCursor("pointer");
			btnChoose.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnChoose.addClickListener(e->{
				openConfirm(model.getName(),model.getId());
			});
			
			if(model.getId().equals(orgId)) {
				btnChoose.setEnabled(false);
			}

			return btnChoose;
		}).setHeader("Thao tác").setWidth("100px").setFlexGrow(0);

		grid.setWidthFull();
		grid.setAllRowsVisible(true);
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

		horizontalLayout.add(txtSearch,btnSearch,btnChooseManagert);
		horizontalLayout.expand(txtSearch);
		horizontalLayout.setWidthFull();


		return horizontalLayout;
	}
	
	private void openConfirmMoveOrgToManagert() {
		ConfirmDialogTemplate confirm = new ConfirmDialogTemplate("Chuyển đơn vị lên các cấp cao nhất");
		confirm.setText("Xác nhận chuyển đơn vị");
		confirm.addConfirmListener(e->{
			moveOrgToManagert();
		});
		confirm.setCancelable(true);
		confirm.open();
	} 
	
	private void openConfirm(String name,String parentId) {
		ConfirmDialogTemplate confirm = new ConfirmDialogTemplate("Chuyển đơn vị vào trong - "+name);
		confirm.setText("Xác nhận chuyển đơn vị");
		confirm.addConfirmListener(e->{
			if(checkChooseManagert == true) {
				moveOrgToManagert();
			}else {
				updateOrg(parentId);
			}
		});
		confirm.setCancelable(true);
		confirm.open();
	} 

	private void updateOrg(String parentId) {
		CreateAndUpdateOrgModel createAndUpdateOrgModel = new CreateAndUpdateOrgModel();

		try {
			ApiResultResponse<ApiOrganizationModel> getData = ApiOrganizationService.getOneOrg(orgId);
			createAndUpdateOrgModel = new CreateAndUpdateOrgModel(getData.getResult());
			createAndUpdateOrgModel.setParentId(parentId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		doUpdate(createAndUpdateOrgModel);
	}

	private void moveOrgToManagert() {
		CreateAndUpdateOrgModel createAndUpdateOrgModel = new CreateAndUpdateOrgModel();

		try {
			ApiResultResponse<ApiOrganizationModel> getData = ApiOrganizationService.getOneOrg(orgId);
			createAndUpdateOrgModel = new CreateAndUpdateOrgModel(getData.getResult());
			createAndUpdateOrgModel.setParentId(null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		doUpdate(createAndUpdateOrgModel);
	}

	private void doUpdate(CreateAndUpdateOrgModel createAndUpdateOrgModel) {
		try {
			ApiCreateAndUpdateOrgModel andUpdateOrgModel = new ApiCreateAndUpdateOrgModel(createAndUpdateOrgModel);
			ApiResultResponse<Object> update = ApiOrganizationService.updateOrg(orgId, andUpdateOrgModel);
			if(update.getStatus() == 200) {
				loadData();
				NotificationTemplate.success(update.getMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}



















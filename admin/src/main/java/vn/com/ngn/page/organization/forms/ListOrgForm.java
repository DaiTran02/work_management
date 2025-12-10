package vn.com.ngn.page.organization.forms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.mapping.ApiMappingService;
import vn.com.ngn.api.organization.ApiKeyAndValueOrgModel;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryFilterModel;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryModel;
import vn.com.ngn.api.organization_category.ApiOrganizationCategoryService;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.ListUserIDsModel;
import vn.com.ngn.page.organization.models.OrganizationModel;
import vn.com.ngn.page.organization.models.UserOrganizationExpandsModel;
import vn.com.ngn.page.user.form.EditUserForm;
import vn.com.ngn.utils.CheckPermissionUtil;
import vn.com.ngn.utils.PropUtils;
import vn.com.ngn.utils.SessionUtil;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.HeaderComponent;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.OrganizationNavForm;
import vn.com.ngn.utils.components.PaginationForm;

public class ListOrgForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private CheckPermissionUtil checkPermissionUtil = new CheckPermissionUtil();
	private VerticalLayout navLayout = new VerticalLayout();
	private HorizontalLayout hFolderlayout = new HorizontalLayout();
	private VerticalLayout contentLayout = new VerticalLayout();

	private OrganizationNavForm organizationNavForm = new OrganizationNavForm();
	private PaginationForm paginationForm;
	private Grid<OrganizationModel> grid = new Grid<OrganizationModel>(OrganizationModel.class,false);
	private List<OrganizationModel> listOrg = new ArrayList<OrganizationModel>();
	private List<OrganizationModel> listSelect = new ArrayList<OrganizationModel>();
	
	private List<UserOrganizationExpandsModel> listUserChange = new ArrayList<UserOrganizationExpandsModel>();

	private HeaderComponent headerLayoutUser = new HeaderComponent("Người dùng thuộc đơn vị");
	private ListUserOrgExpandsForm listUserOrgExpandsForm;

	private H5	titleOrg = new H5();

	private TextField txtSearch = new TextField();
	private ComboBox<Pair<String, String>> cmbTypeOfOrg = new ComboBox<Pair<String,String>>();
	private ComboBox<Pair<String, String>> cmbLevelOfOrg = new ComboBox<Pair<String,String>>();
	private Checkbox cbActive = new Checkbox("Hoạt động");
	private ButtonTemplate btnSearch = new ButtonTemplate(FontAwesome.Solid.SEARCH.create());
	
	private ButtonTemplate btnMappingOrg = new ButtonTemplate("Cập nhật",FontAwesome.Solid.EDIT.create());
	
	private ButtonTemplate btnAddOrg = new ButtonTemplate("Thêm đơn vị",FontAwesome.Solid.PLUS.create());
	
	private ButtonTemplate btnMoveManager = new ButtonTemplate("Chuyển đơn vị chủ quản",FontAwesome.Solid.FOLDER_TREE.create());
	
	private ButtonTemplate btnGroupOrgExpands = new ButtonTemplate("Nhóm làm việc",FontAwesome.Solid.USERS.create());
	private ButtonTemplate btnRole = new ButtonTemplate("Vai trò",FontAwesome.Regular.FILE_ALT.create());
	private ButtonTemplate btnUserDivision = new ButtonTemplate("Phân người dùng",FontAwesome.Solid.HOME_USER.create());
	
	private ButtonTemplate btnDeleteSelectList = new ButtonTemplate("Xóa",FontAwesome.Solid.TRASH.create());

	private ButtonTemplate btnBackToOldOrg = new ButtonTemplate(FontAwesome.Solid.ARROW_LEFT.create());
	
	private List<ApiOrganizationCategoryModel> listOrgCategory = new ArrayList<ApiOrganizationCategoryModel>();

	
	private String parentId = null;
	public ListOrgForm() {
		Pair<String, String> item;
		if(checkPermissionUtil.checkOrg() == false) {
			item = Pair.of(null,"Các tổ chức cấp cao nhất");
		}else {
			item = Pair.of(SessionUtil.getOrgId(),SessionUtil.getDetailOrg().getOrganizationName());
			parentId = SessionUtil.getOrgId();
		}
		organizationNavForm.addItem(item);
		
		
		initCmbLevelOfOrg();
		initCmbTypeOfOrg();
		buildLayout();
		configComponent();
		loadData();
//		changeWidthLayout() ;
		loadOrgCategory();
		loadLayoutUserOrg();
		
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		titleOrg.getStyle().set("margin-top", "11px");

		btnBackToOldOrg.getStyle().setCursor("pointer");
		btnBackToOldOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		hFolderlayout.getStyle().set("border-bottom", "1px solid #a7d6ff");
		hFolderlayout.setWidthFull();
		hFolderlayout.expand(titleOrg);

		paginationForm = new PaginationForm(()->{
			if(paginationForm!=null) {
				loadData();
			}
		});


		if(organizationNavForm.getCurrentItem().getLeft()==null) {
			titleOrg.removeAll();
			titleOrg.add("Danh sách tổ chức");
			hFolderlayout.add(titleOrg);
			contentLayout.add(paginationForm,createGridOrg());
		}else {
			titleOrg.removeAll();
			titleOrg.add(SessionUtil.getDetailOrg().getOrganizationName());
			hFolderlayout.add(titleOrg);
			contentLayout.add(paginationForm,createGridOrg());
		}


		btnDeleteSelectList.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnDeleteSelectList.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
		btnDeleteSelectList.addThemeVariants(ButtonVariant.LUMO_ERROR);
		btnDeleteSelectList.setEnabled(false);
		btnDeleteSelectList.setTooltipText("Xóa các đơn vị đã chọn là con của đơn vị này");

		btnGroupOrgExpands.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnGroupOrgExpands.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
		btnGroupOrgExpands.setEnabled(false);
		btnGroupOrgExpands.setTooltipText("Quản lý các nhóm thuộc đơn vị");

		btnUserDivision.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnUserDivision.setEnabled(false);
		btnUserDivision.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
		btnUserDivision.setTooltipText("Chuyển người dùng vào hoặc ra khỏi đơn vị");
		
		btnMoveManager.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnMoveManager.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
		btnMoveManager.setEnabled(false);
		btnMoveManager.setTooltipText("Di chuyển đơn vị vào trong hoặc ra ngoài các đơn vị khác");

		btnRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnRole.setEnabled(false);
		btnRole.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
		btnRole.setTooltipText("Quản lý các vai trò thuộc đơn vị");
		

		hFolderlayout.add(btnMappingOrg,btnMoveManager,btnUserDivision,btnRole,btnGroupOrgExpands,btnDeleteSelectList);
		navLayout.add(organizationNavForm,hFolderlayout,createToolbar());
		navLayout.setWidth("100%");
		navLayout.setPadding(false);
		
		//File css in /component/orgnav.css
//		navLayout.addClassName("nav_org");

		contentLayout.setSizeFull();
		contentLayout.setPadding(false);
//		contentLayout.getStyle().set("margin-top", "11%").set("margin-bottom", "10px");
		this.add(navLayout,contentLayout);
	}

	@Override
	public void configComponent() {
		
		cbActive.addClickListener(e->{
			loadData();
		});
		
		organizationNavForm.addClickListener(e->{
			Pair<String, String> itemNav = organizationNavForm.getCurrentItem();
			this.parentId = itemNav.getLeft();
			loadData();
			if(itemNav.getLeft()==null) {
				hFolderlayout.removeAll();
				titleOrg.removeAll();
				titleOrg.add("Danh sách tổ chức");
				hFolderlayout.add(titleOrg);
				hFolderlayout.add(btnMappingOrg,btnMoveManager,btnUserDivision,btnRole,btnGroupOrgExpands,btnDeleteSelectList);
				if(checkPermissionUtil.checkOrg() == false) {
					contentLayout.remove(headerLayoutUser);
				}
			}else {
				hFolderlayout.removeAll();
				titleOrg.removeAll();
				titleOrg.add(itemNav.getRight());
				if(checkPermissionUtil.checkOrg() == false) {
					btnBackToOldOrg.setTooltipText("Quay lại "+organizationNavForm.getItem().getRight());
				}else {
					btnBackToOldOrg.setTooltipText("");
				}
				
				if(itemNav.getKey().equals(SessionUtil.getOrgId())) {
					btnBackToOldOrg.setVisible(false);
				}else {
					btnBackToOldOrg.setVisible(true);
				}

				hFolderlayout.add(btnBackToOldOrg,titleOrg);
				hFolderlayout.add(btnMoveManager,btnUserDivision,btnRole,btnGroupOrgExpands,btnDeleteSelectList);

			}


		});


		btnBackToOldOrg.addClickListener(e->{
			Pair<String, String> itemNav = organizationNavForm.getItem();

			organizationNavForm.removeItem(itemNav);
			organizationNavForm.buildLayout();

			this.parentId = itemNav.getLeft();

			loadData();
			if(itemNav.getLeft()==null) {
				hFolderlayout.removeAll();

				titleOrg.removeAll();
				titleOrg.add("Danh sách tổ chức");

				hFolderlayout.add(titleOrg);
				hFolderlayout.add(btnMappingOrg,btnMoveManager,btnUserDivision,btnRole,btnGroupOrgExpands,btnDeleteSelectList);
				contentLayout.remove(headerLayoutUser);
			}else {
				hFolderlayout.removeAll();

				titleOrg.removeAll();
				titleOrg.add(itemNav.getRight());

				if(checkPermissionUtil.checkOrg() == false) {
					btnBackToOldOrg.setTooltipText("Quay lại "+organizationNavForm.getItem().getRight());
				}else {
					btnBackToOldOrg.setTooltipText("");
				}

				hFolderlayout.add(btnBackToOldOrg,titleOrg);
				hFolderlayout.add(btnMappingOrg,btnMoveManager,btnUserDivision,btnRole,btnGroupOrgExpands,btnDeleteSelectList);
			}

		});

		btnAddOrg.addClickListener(e->{
			openDiaLogCreateOrg(parentId);
		});

		btnDeleteSelectList.addClickListener(e->{
			if(!listSelect.isEmpty()) {
				openConfirmDeleteSelect(listSelect);
			}
		});


		txtSearch.addValueChangeListener(e->{
			loadData();
		});

		btnSearch.addClickListener(e->{
			loadData();
		});

		btnSearch.addClickShortcut(Key.ENTER);

		headerLayoutUser.getBtnAdd().addClickListener(e->{
			openDialogCreateUser(parentId,()->{});
		});
		
		headerLayoutUser.getBtnAddUserFromOrther().addClickListener(e->{
			openDialogImportUser(parentId,()->{});
		});
		
		headerLayoutUser.getBtnSave().addClickListener(e->{
			updateListUserInOrg();
		});
		
		btnMappingOrg.addClickListener(e->{
			openDialogMapping(null);
		});
		
	}

	public void loadData() {
		listOrg = new ArrayList<OrganizationModel>();
		loadLayoutUserOrg();
		try {
			ApiResultResponse<List<ApiOrganizationModel>> getData = ApiOrganizationService.getListOrganization(paginationForm.getSkip(),paginationForm.getLimit(),parentId,txtSearch.getValue(),cbActive.getValue().toString());
			listOrg = getData.getResult().stream().map(OrganizationModel::new).collect(Collectors.toList());
			paginationForm.setItemCount(getData.getTotal());
		} catch (Exception e) {
			e.printStackTrace();
		}
		grid.setItems(listOrg);
		
		if(parentId!=null) {
			loadDataThisOrg(parentId);
		}else {
			loadNewButton();
		}
		
		checkPermission();
	}
	
	private void checkPermission() {
		if(checkPermissionUtil.checkOrg()) {
			btnMoveManager.setVisible(false);
			contentLayout.add(headerLayoutUser);
			listUserOrgExpandsForm.loadData(parentId);
		}
		if(PropUtils.isAllowCreateOrg() == false) {
			if(SessionUtil.getUser().getUsername().equals("supper_admin")) {
				btnAddOrg.setEnabled(true);
			}else {
				btnAddOrg.setEnabled(false);
			}
		}
	}

	private void loadDataThisOrg(String id) {
		
		int countGroup = 0;
		int countRole = 0;
		
		try {
			
			ApiResultResponse<ApiOrganizationModel> dataOrg = ApiOrganizationService.getOneOrg(id);
			
			countGroup = dataOrg.getResult().getGroupOrganizationExpands().size();
			countRole = dataOrg.getResult().getRoleOrganizationExpands().size();

			btnGroupOrgExpands = new ButtonTemplate("Nhóm làm việc ("+countGroup+")",FontAwesome.Solid.USERS.create());
			btnGroupOrgExpands.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnGroupOrgExpands.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
			btnGroupOrgExpands.setTooltipText("Quản lý các nhóm thuộc đơn vị");

			btnRole = new ButtonTemplate("Vai trò ("+countRole+")",FontAwesome.Regular.FILE_ALT.create());
			btnRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnRole.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
			btnRole.setTooltipText("Quản lý các vai trò thuộc đơn vị");
			
			btnUserDivision = new ButtonTemplate("Phân người dùng",FontAwesome.Solid.HOME_USER.create());
			btnUserDivision.setEnabled(true);
			btnUserDivision.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnUserDivision.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
			btnUserDivision.setTooltipText("Chuyển người dùng vào hoặc ra khỏi đơn vị");
			
			btnMoveManager = new ButtonTemplate("Chuyển đơn vị chủ quản",FontAwesome.Solid.FOLDER_TREE.create());
			btnMoveManager.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnMoveManager.setEnabled(true);
			btnMoveManager.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
			btnMoveManager.setTooltipText("Di chuyển đơn vị vào trong hoặc ra ngoài các đơn vị khác");
			
			btnMappingOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnMappingOrg.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
			btnMappingOrg.setTooltipText("Cập nhật toàn đơn vị");

			listUserOrgExpandsForm.loadData(id);

			configButtonWhenParentNotNull(dataOrg.getResult().getName(),dataOrg.getResult().getId(),(String)dataOrg.getResult().getParentId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		hFolderlayout.removeAll();
		if(id.equals(SessionUtil.getOrgId())) {
			btnBackToOldOrg.setVisible(false);
		}else {
			btnBackToOldOrg.setVisible(true);
		}
		hFolderlayout.add(btnBackToOldOrg,titleOrg);
		hFolderlayout.add(btnMappingOrg,btnMoveManager,btnUserDivision,btnRole,btnGroupOrgExpands,btnDeleteSelectList);
		
	}

	private void loadNewButton() {
		btnGroupOrgExpands = new ButtonTemplate("Nhóm làm việc",FontAwesome.Solid.USERS.create());
		btnGroupOrgExpands.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnGroupOrgExpands.setEnabled(false);
		btnGroupOrgExpands.setTooltipText("Quản lý các nhóm thuộc đơn vị");
		btnGroupOrgExpands.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");

		btnRole = new ButtonTemplate("Vai trò",FontAwesome.Regular.FILE_ALT.create());
		btnRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnRole.setEnabled(false);
		btnRole.setTooltipText("Quản lý các vai trò thuộc đơn vị");
		btnRole.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");

		btnUserDivision = new ButtonTemplate("Phân người dùng",FontAwesome.Solid.HOME_USER.create());
		btnUserDivision.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnUserDivision.setEnabled(false);
		btnUserDivision.setTooltipText("Chuyển người dùng vào hoặc ra khỏi đơn vị");
		btnUserDivision.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
		
		btnMoveManager = new ButtonTemplate("Chuyển đơn vị chủ quản",FontAwesome.Solid.FOLDER_TREE.create());
		btnMoveManager.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnMoveManager.setEnabled(false);
		btnMoveManager.setTooltipText("Di chuyển đơn vị vào trong hoặc ra ngoài các đơn vị khác");
		btnMoveManager.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
		
		btnMappingOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnMappingOrg.getStyle().setCursor("pointer").set("border", "1px solid").set("border-radius", "20px").set("margin-top", "-5px");
		btnMappingOrg.setTooltipText("Cập nhật toàn đơn vị");

	}
	
	private void loadOrgCategory() {
		listOrgCategory = new ArrayList<ApiOrganizationCategoryModel>();
		ApiOrganizationCategoryFilterModel apiOrganizationCategoryFilterModel = new ApiOrganizationCategoryFilterModel();
		apiOrganizationCategoryFilterModel.setSkip(0);
		apiOrganizationCategoryFilterModel.setLimit(0);
		apiOrganizationCategoryFilterModel.setActive(true);
		
		try {
			ApiResultResponse<List<ApiOrganizationCategoryModel>> data = ApiOrganizationCategoryService.getListOrganizationCategory(apiOrganizationCategoryFilterModel);
			data.getResult().stream().forEach(model->{
				listOrgCategory.add(model);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void configButtonWhenParentNotNull(String nameOrg,String orgId,String idParent) {
		btnUserDivision.addClickListener(e->{
			openDialogUserDevision();
		});

		btnGroupOrgExpands.addClickListener(e->{
			openDialogGroup(parentId);
		});
		
		btnRole.addClickListener(e->{
			openDialogControlRole();
		});
		btnMoveManager.addClickListener(e->{
			openDialogMoveManager(nameOrg,orgId,idParent);
		});
		
	}

//	private void changeWidthLayout() {
//		UI.getCurrent().getPage().addBrowserWindowResizeListener(e->{
//			if(e.getWidth()>1537) {
//				navLayout.setWidth("87%");
//				contentLayout.getStyle().set("margin-top", "8%");
//			}else {
//				navLayout.setWidth("85%");
//				contentLayout.getStyle().set("margin-top", "11%");
//			}
//		});
//		
//		UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
//			if(e.getScreenWidth()>1537) {
//				navLayout.setWidth("87%");
//				contentLayout.getStyle().set("margin-top", "8%");
//			}else {
//				navLayout.setWidth("85%");
//				contentLayout.getStyle().set("margin-top", "11%");
//			}
//		});
//		
//	}

	private Component createGridOrg() {
		grid = new Grid<OrganizationModel>(OrganizationModel.class,false);

		grid.addColumn(OrganizationModel::getOrder).setHeader("STT").setWidth("35px").setFlexGrow(0);
		grid.addComponentColumn(model->{
			Button btnFolder = new Button(model.getName());
			btnFolder.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnFolder.getStyle().setCursor("pointer");
			btnFolder.addClickListener(e->{
				paginationForm.refreshIndexCurrent();
				Pair<String, String> item = Pair.of(model.getId(),model.getName() + " ("+model.getUnitCode()+")");
				organizationNavForm.addItem(item);
				this.parentId = model.getId();

				hFolderlayout.removeAll();

				titleOrg.removeAll();
				titleOrg.add(model.getName());

				hFolderlayout.add(btnBackToOldOrg,titleOrg);
				hFolderlayout.add(btnMappingOrg,btnMoveManager,btnUserDivision,btnRole,btnGroupOrgExpands,btnDeleteSelectList);

				btnBackToOldOrg.setTooltipText("Quay lại "+organizationNavForm.getItem().getRight());
				
				if(item.getKey().equals(SessionUtil.getOrgId())) {
					btnBackToOldOrg.setVisible(false);
				}else {
					btnBackToOldOrg.setVisible(true);
				}

				loadLayoutUserOrg();
				listUserOrgExpandsForm.loadData(model.getId());

				contentLayout.add(headerLayoutUser);
				loadData();
			});

			String text = String.valueOf(model.getCountSubOrganization()+" Đơn vị trực thuộc và ");

			Span span = new Span(text);
			span.getStyle().set("font-size", "12px");
			span.getStyle().set("margin-left", "6px");
			span.getStyle().set("font-style", "italic");
			
			ButtonTemplate btnUser = new ButtonTemplate(model.getUserOrganizationExpands().size()+ " Người dùng");
			btnUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnUser.getStyle().set("font-size", "12px").set("font-style", "italic").setMargin("0").setPadding("0").set("padding-left", "4px");
			btnUser.setHeight("0");
			btnUser.addClickListener(e->{
				openDialogUserOfOrg(model);
			});
			
			HorizontalLayout hLayoutHelper = new HorizontalLayout();
			hLayoutHelper.add(span,btnUser);
			hLayoutHelper.getStyle().set("display", "flex").set("align-items", "center");
			hLayoutHelper.setSpacing(false);


			VerticalLayout vlayout = new VerticalLayout();
			vlayout.setSpacing(false);
			vlayout.setPadding(false);
			vlayout.add(btnFolder,hLayoutHelper);
			System.out.println(parentId);

			return vlayout;
		}).setHeader("Tên đơn vị").setWidth("210px").setFlexGrow(0).setResizable(true);
		grid.addColumn(OrganizationModel::getDescription).setHeader("Mô tả");
		grid.addColumn(OrganizationModel::getUnitCode).setHeader("Mã đơn vị quốc gia");
		grid.addComponentColumn(model->{
			Span span = new Span();
			for(ApiOrganizationCategoryModel apiOrganizationCategoryModel : listOrgCategory) {
				if(model.getOrganizationCategoryId() != null) {
					if(model.getOrganizationCategoryId().equals(apiOrganizationCategoryModel.getId())) {
						span = new Span(apiOrganizationCategoryModel.getName());
						break;
					}
				}
			}
			return span;
		}).setHeader("Loại đơn vị");
		grid.addColumn(model->{
			return model.getLevel() != null ? model.getLevel().getName() : "Đang cập nhật";
		}).setHeader("Cấp đơn vị");
		
		grid.addComponentColumn(model->{
			return new Span(model.getUpdateTimeText());
		}).setHeader("Ngày cập nhật").setWidth("110px").setFlexGrow(0);
		
		grid.addComponentColumn(model->{
			if(model.isActive()) {
				return createStatusIcon("Available");
			}else {
				return createStatusIcon("NotAvailable");
			}
		}).setHeader("Tình trạng").setWidth("150px").setFlexGrow(0);
		grid.addComponentColumn(model->{
			ButtonTemplate btnGroup = new ButtonTemplate(model.getGroupOrganizationExpands().size() + " Nhóm");
			
			btnGroup.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnGroup.addClickListener(e->{
				openDialogGroup(model.getId());
			});
			
			return btnGroup;
		}).setWidth("130px").setFlexGrow(0).setHeader("Nhóm làm việc");
		grid.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();

			Button btnEditOrg = new Button(FontAwesome.Solid.EDIT.create());
			btnEditOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEditOrg.getStyle().setCursor("pointer");
			btnEditOrg.setTooltipText("Chỉnh sửa");
			btnEditOrg.addClickListener(e->{
				openDialogUpdateOrg(model.getId());
			});

			Button btnDeleteOrg = new Button(FontAwesome.Solid.TRASH.create());
			btnDeleteOrg.addThemeVariants(ButtonVariant.LUMO_ERROR);
			btnDeleteOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnDeleteOrg.setTooltipText("Xóa");
			btnDeleteOrg.getStyle().setCursor("pointer");
			btnDeleteOrg.addClickListener(e->{
				openConfirmDelete(model.getId());
			});

			if(model.getCountSubOrganization()>0||model.getUserOrganizationExpands().size()>0) {
				btnDeleteOrg.setEnabled(false);
			}
			
			ButtonTemplate btnMappingOrg = new ButtonTemplate(FontAwesome.Solid.REFRESH.create());
			btnMappingOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnMappingOrg.setTooltipText("Cập nhật đơn vị này");
			btnMappingOrg.addClickListener(e->{
				openDialogMapping(model.getId());
			});
			

			hLayout.add(btnEditOrg,btnDeleteOrg,btnMappingOrg);

			return hLayout;
		}).setHeader("Thao tác").setWidth("150px").setFlexGrow(0);

		grid.setSelectionMode(Grid.SelectionMode.MULTI);
		grid.addSelectionListener(model->{
			btnDeleteSelectList.setEnabled(true);

			listSelect.clear();
			listSelect.addAll(model.getAllSelectedItems());
			if(listSelect.isEmpty()) {
				btnDeleteSelectList.setEnabled(false);
			}
			model.getAllSelectedItems().forEach(e->{
				if(e.getCountSubOrganization()>0||e.getUserOrganizationExpands().size()>0) {
					grid.getSelectionModel().deselect(e);
				}
			});

		});


		grid.setWidthFull();
//		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

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

	private Component createToolbar() {
		HorizontalLayout hLayout = new HorizontalLayout();

		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Tìm kiếm...");
		txtSearch.setClearButtonVisible(true);
		
		btnSearch.getStyle().setCursor("pointer");
//		btnAddOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		cbActive.setValue(true);
		cbActive.setWidth("150px");
		cbActive.getStyle().set("margin-top", "5px");
		
		cmbTypeOfOrg.setHelperText("Loại đơn vị");
		cmbLevelOfOrg.setHelperText("Cấp đơn vị");
		
		hLayout.add(cmbTypeOfOrg,cmbLevelOfOrg,txtSearch,cbActive,btnSearch,btnAddOrg);
		hLayout.expand(txtSearch);
		hLayout.setWidthFull();
		return hLayout;
	}

	private void initCmbTypeOfOrg() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		
		ApiOrganizationCategoryFilterModel apiOrganizationCategoryFilterModel = new ApiOrganizationCategoryFilterModel();
		apiOrganizationCategoryFilterModel.setSkip(0);
		apiOrganizationCategoryFilterModel.setLimit(0);
		apiOrganizationCategoryFilterModel.setActive(true);
		
		try {
			ApiResultResponse<List<ApiOrganizationCategoryModel>> listApiResultResponse = ApiOrganizationCategoryService.getListOrganizationCategory(apiOrganizationCategoryFilterModel);
			listApiResultResponse.getResult().forEach(model->{
				listData.add(Pair.of(model.getId(),model.getName()));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		cmbTypeOfOrg.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		cmbTypeOfOrg.setItems(listData);
		cmbTypeOfOrg.setItemLabelGenerator(Pair::getValue);
		cmbTypeOfOrg.setValue(listData.get(0));
	}
	
	private void initCmbLevelOfOrg() {
		List<Pair<String, String>> listData = new ArrayList<Pair<String,String>>();
		listData.add(Pair.of(null,"Tất cả"));
		
		try {
			ApiResultResponse<List<ApiKeyAndValueOrgModel>> data = ApiOrganizationService.getLevels();
			if(data.isSuscces()) {
				data.getResult().forEach(model->{
					listData.add(Pair.of(model.getKey(),model.getName()));
				});
			}
		} catch (Exception e) {
			
		}
		
		cmbLevelOfOrg.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
		cmbLevelOfOrg.setItems(listData);
		cmbLevelOfOrg.setItemLabelGenerator(Pair::getValue);
		cmbLevelOfOrg.setValue(listData.get(0));
	}

	private void loadLayoutUserOrg() {
		headerLayoutUser.removeAll();
		VerticalLayout layout = new VerticalLayout();
		listUserOrgExpandsForm = new ListUserOrgExpandsForm(parentId,true);
		if(parentId!=null) {
			layout.add(listUserOrgExpandsForm);
		}
		
		
		
		listUserOrgExpandsForm.addChangeListener(e->{
			headerLayoutUser.getBtnSave().setVisible(true);
			listUserChange.clear();
			listUserChange.addAll(listUserOrgExpandsForm.getListUser());
		});
		
		layout.add(listUserOrgExpandsForm);
		layout.setPadding(false);
		layout.setSpacing(false);

		headerLayoutUser.add(layout);
	}
	
	private void openDialogCreateUser(String idOrg,Runnable run) {
		DialogTemplate dialog = new DialogTemplate("THÊM TÀI KHOẢN NGƯỜI DÙNG",()->{

		});

		EditUserForm editUserForm = new EditUserForm(null,true,idOrg,()->{
			loadData();
			run.run();
			NotificationTemplate.success("Thành công");
			dialog.close();
		});


		dialog.getBtnSave().addClickListener(e->{
			editUserForm.doSave();
		});
		
		dialog.getBtnSave().addClickShortcut(Key.ENTER);

		dialog.add(editUserForm);
		dialog.setWidth("60%");

		dialog.open();
	}

	private void openDiaLogCreateOrg(String parentId) {
		DialogTemplate dialog = new DialogTemplate("THÊM ĐƠN VỊ",()->{

		});
		EditOrgForm editOrgForm = new EditOrgForm(null,parentId,()->{
			loadData();
			NotificationTemplate.success("Thành công");
			dialog.close();
		});
		dialog.add(editOrgForm);
		dialog.setWidth("60%");
		dialog.getBtnSave().addClickListener(e->{
			editOrgForm.saveOrg();
		});
		dialog.getBtnSave().addClickShortcut(Key.ENTER);
		
		dialog.open();
	}

	private void openDialogUpdateOrg(String idOrg) {
		DialogTemplate dialog = new DialogTemplate("CHỈNH SỬA ĐƠN VỊ",()->{

		});
		EditOrgForm editOrgForm = new EditOrgForm(idOrg, parentId, ()->{
			loadData();
			NotificationTemplate.success("Thành công");
			dialog.close();
		});

		dialog.add(editOrgForm);
		dialog.getBtnSave().addClickListener(e->{
			editOrgForm.saveOrg();
		});
		dialog.setWidth("60%");
		dialog.open();
	}

	private void openConfirmDelete(String idOrg) {
		ConfirmDialogTemplate confirm = new ConfirmDialogTemplate("Xóa tổ chức");
		confirm.setText("Xác nhận xóa tổ chức");
		confirm.addConfirmListener(e->{
			doDeleteOrg(idOrg);
			NotificationTemplate.success("Xóa tổ chức thành công");
			loadData();
		});
		confirm.setConfirmButtonTheme("error");
		confirm.setCancelable(true);
		confirm.open();
	}

	private void openConfirmDeleteSelect(List<OrganizationModel> listSL) {
		ConfirmDialogTemplate confirm = new ConfirmDialogTemplate("Xóa các tổ chức đã chọn");
		confirm.setText("Xác nhận xóa các tổ chức đã chọn, không thể phục hồi");
		confirm.addConfirmListener(e->{
			listSL.stream().forEach(model->{
				if(model.getCountSubOrganization()==0||model.getUserOrganizationExpands().size()==0) {
					doDeleteOrg(model.getId());
				}
			});
			loadData();
			NotificationTemplate.success("Xóa các tổ chức đã chọn thành công");
		});

		confirm.setConfirmButtonTheme("error");
		confirm.setCancelable(true);
		confirm.open();
	}

	private void openDialogGroup(String parentId) {
		DialogTemplate dialog = new DialogTemplate("NHÓM LÀM VIỆC",()->{
			loadData();
		});

		GroupOrgExpandsForm groupOrgExpandsForm = new GroupOrgExpandsForm(parentId,()->{
			loadData();
		});

		dialog.getFooter().removeAll();
		dialog.add(groupOrgExpandsForm);
		dialog.setSizeFull();
		dialog.open();
	}
	
	private void openDialogUserDevision() {
		DialogTemplate dialog = new DialogTemplate("PHÂN NGƯỜI DÙNG",()->{
			loadData();
		});

		TabDevisionUserForm tabDevisionUserForm = new TabDevisionUserForm(parentId);

		dialog.getBtnSave().addClickListener(e->{
			loadData();
			dialog.close();
		});
		
		dialog.getFooter().removeAll();
		dialog.add(tabDevisionUserForm);
		dialog.setWidth("99%");
		dialog.setHeight("100%");
		dialog.open();
	}

	private void openDialogControlRole() {
		DialogTemplate dialog = new DialogTemplate("DANH SÁCH VAI TRÒ",()->{
			loadData();
		});

		RoleOrgExpandsForm roleOrgExpandsForm = new RoleOrgExpandsForm(parentId,null);

		dialog.getFooter().removeAll();
		
		dialog.add(roleOrgExpandsForm);
		dialog.setWidth("100%");
		dialog.setHeight("99%");
		dialog.open();
	}
	
	private void openDialogMoveManager(String nameOrg,String orgId,String idParent) {
		DialogTemplate dialog = new DialogTemplate("CHUYỂN ĐƠN VỊ CHỦ QUẢN - "+nameOrg,()->{
			loadData();
		});

		MoveManagerForm moveManagerForm = new MoveManagerForm(orgId,idParent);

		dialog.getBtnSave().addClickListener(e->{
			loadData();
			dialog.close();
		});
		dialog.add(moveManagerForm);
		dialog.setWidth("100%");
		dialog.setHeight("99%");
		dialog.open();
	}


	private void doDeleteOrg(String idOrg) {
		try {
			ApiResultResponse<Object> delete = ApiOrganizationService.deleteOrg(idOrg);
			delete.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateListUserInOrg() {
		ListUserIDsModel listUserIDsModel = new ListUserIDsModel();
		try {
			ApiResultResponse<List<ApiUserModel>> getListUser = ApiOrganizationService.getListUserIncludeOrg(parentId, "");
			getListUser.getResult().stream().forEach(model->{
				listUserIDsModel.getUserIds().add(model.getId());
			});
			ApiResultResponse<Object> removeUser = ApiOrganizationService.removeUsersFormOrg(parentId, listUserIDsModel);
			if(removeUser.isSuscces()) {
				doUpdateUserInOrg();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doUpdateUserInOrg() {
		try {
			ListUserIDsModel listUserIDsModel = new ListUserIDsModel();
			listUserChange.stream().forEach(model->{
				listUserIDsModel.getUserIds().add(model.getUserId());
			});
			ApiResultResponse<Object> moveUser = ApiOrganizationService.moveUsersToOrg(parentId, listUserIDsModel);
			if(moveUser.isSuscces()) {
				loadData();
				headerLayoutUser.getBtnSave().setVisible(false);
				NotificationTemplate.success("Đã lưu thứ tự danh sách mới");
			}
		} catch (Exception e) {
		}
	}
	
	private void openDialogUserOfOrg(OrganizationModel organizationModel) {
		DialogTemplate dialogTemplate = new DialogTemplate("Danh sách người dùng thuộc đơn vị "+organizationModel.getName(),()->{});
		
		VerticalLayout vLayoutUserOfOrg = new VerticalLayout();
		vLayoutUserOfOrg.setSizeFull();
		
		ListUserOrgExpandsForm listUserOrgExpandsForm = new ListUserOrgExpandsForm(organizationModel.getId(),false);
		
		ButtonTemplate btnAddUser = new ButtonTemplate("Thêm người dùng mới",FontAwesome.Solid.PLUS.create());
		
		ButtonTemplate btnImportUser = new ButtonTemplate("Thêm người dùng có sẵn",FontAwesome.Solid.FILE_IMPORT.create());
		btnImportUser.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		
		btnAddUser.addClickListener(e->{
			openDialogCreateUser(organizationModel.getId(),()->{
				listUserOrgExpandsForm.loadData(organizationModel.getId());
			});
		});
		
		btnImportUser.addClickListener(e->{
			openDialogImportUser(organizationModel.getId(),()->{
				listUserOrgExpandsForm.loadData(organizationModel.getId());
			});
		});
		
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.add(btnAddUser,btnImportUser);
		hLayoutButton.getStyle().set("margin-left", "auto");
		
		vLayoutUserOfOrg.add(hLayoutButton,listUserOrgExpandsForm);
		
		dialogTemplate.add(vLayoutUserOfOrg);
		
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		
		dialogTemplate.open();
	}
	
	private void openDialogImportUser(String parentId,Runnable run) {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm người dùng vào đơn vị", ()->{});
		
		UserExcludeOrgForm userExcludeOrgForm = new UserExcludeOrgForm(parentId);
		dialogTemplate.add(userExcludeOrgForm);
		userExcludeOrgForm.addChangeListener(e->{
			loadData();
			run.run();
		});
		
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setHeightFull();
		dialogTemplate.setWidth("70%");
		dialogTemplate.open();
	}
	
	private void openDialogMapping(String idOrg) {
		ConfirmDialogTemplate dialogTemplate = new ConfirmDialogTemplate("Cập nhật đơn vị");

		Random random = new Random();
		int result = 0;
		if(idOrg == null) {
			result = random.nextInt(30-20+1) + 15;
		}else {
			result = random.nextInt(5-1+1);
		}
		
		VerticalLayout vLayout = new VerticalLayout();
		vLayout.add(new H5("Hệ thống sẽ cập nhật đơn vị theo hệ thống quản lý văn bản"));
		vLayout.add(new Html("<div>- Thời gian dự kiến hoàn thành <b> "+result+" phút</b>, trong quá trình cập nhật đơn vị hệ thống vẫn hoạt động bình thường.</div>"),
				new Span("- Sau khi hoàn thành các đơn vị sẽ được cập nhật theo hệ thống quản lý văn bản, người dùng đang ở sai phòng sẽ được thêm vào phòng theo hệ thống quản lý văn bản, "
						+ "người quản lý đơn vị có thể vào phòng hoặc đơn vị để xóa người dùng khỏi phòng."));
		
		dialogTemplate.add(vLayout);
		
		dialogTemplate.addConfirmListener(e->{
			if(idOrg == null) {
				doMapping();
			}else {
				doMappingByOrg(idOrg);
			}
		});
		
		dialogTemplate.setCancelable(true);
		
		dialogTemplate.open();
	}
	
	private void doMapping() {
		try {
			ApiResultResponse<String> mapping = ApiMappingService.mappingAllOrg();
			if(mapping.isSuscces()) {
				NotificationTemplate.success("Hệ thống đang thực hiện cập nhật đơn vị");
			}
		} catch (Exception e) {
			NotificationTemplate.error("Có lỗi xảy ra vui lòng liên hệ quản trị");
		}
	}
	
	private void doMappingByOrg(String idOrg) {
		try {
			ApiResultResponse<String> mapping = ApiMappingService.mappingByIdOrg(idOrg);
			if(mapping.isSuscces()) {
				NotificationTemplate.success("Hệ thống đang thực hiện cập nhật đơn vị");
			}
		} catch (Exception e) {
			NotificationTemplate.error("Có lỗi xảy ra vui lòng liên hệ quản trị");
		}
	}


}

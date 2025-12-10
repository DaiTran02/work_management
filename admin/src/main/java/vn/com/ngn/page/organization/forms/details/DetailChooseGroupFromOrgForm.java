package vn.com.ngn.page.organization.forms.details;

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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiGroupOrganizationExpandsModel;
import vn.com.ngn.api.organization.ApiOrganizationModel;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.user.ApiUserModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.ListUserIDsModel;
import vn.com.ngn.page.organization.models.OrganizationModel;
import vn.com.ngn.page.organization.models.UserOrganizationExpandsModel;
import vn.com.ngn.page.user.model.UserModel;
import vn.com.ngn.utils.components.ConfirmDialogTemplate;
import vn.com.ngn.utils.components.NotificationTemplate;
import vn.com.ngn.utils.components.OrganizationNavForm;
import vn.com.ngn.utils.components.PaginationForm;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class DetailChooseGroupFromOrgForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private VerticalLayout navLayout = new VerticalLayout();
	private HorizontalLayout hFolderLayout = new HorizontalLayout();
	private VerticalLayout contentLayout = new VerticalLayout();

	private OrganizationNavForm organizationNavForm = new OrganizationNavForm();
	private PaginationForm paginationForm;

	private Grid<OrganizationModel> grid = new Grid<OrganizationModel>(OrganizationModel.class,false);
	private List<OrganizationModel> listModel = new ArrayList<OrganizationModel>();
	
	private List<OrganizationModel> listOrgIsChoose = new ArrayList<OrganizationModel>();
	
	private List<UserModel> listUserOfOrg = new ArrayList<UserModel>();

	private TextField txtSearch = new TextField();
	private Button btnSearch = new Button(FontAwesome.Solid.SEARCH.create());

	private Button btnBackToOldOrg = new Button(FontAwesome.Solid.ARROW_LEFT.create());
	private H5 titleOrg = new H5();

	private OrganizationModel organizationModel;

	private String thisParentId = null;

	private String orgId;
	public DetailChooseGroupFromOrgForm(String orgId) {
		this.orgId = orgId;
		Pair<String, String> item =  Pair.of(null,"Các tổ chức cấp cao nhất");
		organizationNavForm.addItem(item);

		buildLayout();
		configComponent();
		loadData();
		loadDataUserOfOrg();
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
	}
	

	private Grid<OrganizationModel> createGrid() {
		grid = new Grid<OrganizationModel>(OrganizationModel.class,false);

		grid.addComponentColumn(model->{
			Checkbox cbChoose = new Checkbox();
			
			if(!listOrgIsChoose.isEmpty()) {
				listOrgIsChoose.stream().forEach(check->{
					if(check.getId().equals(model.getId())) {
						cbChoose.setValue(true);
					}
				});
			}
			
			cbChoose.addClickListener(e->{
				if(cbChoose.getValue() == true) {
					listOrgIsChoose.add(model);
				}else {
					listOrgIsChoose.remove(model);
				}
			});
			
			return cbChoose;
		}).setWidth("40px").setFlexGrow(0);
		
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
				btnBackToOldOrg.setTooltipText("Quay lại "+organizationNavForm.getItem().getRight());
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

		grid.setWidthFull();
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

		return grid;
	}

	public void loadDataUserOfOrg() {
		listUserOfOrg = new ArrayList<UserModel>();
		try {
			ApiResultResponse<List<ApiUserModel>> getListUser = ApiOrganizationService.getListUserIncludeOrg(orgId, txtSearch.getValue());
			listUserOfOrg = getListUser.getResult().stream().map(UserModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public void createGroup() {
		List<String> listIdUserInOrg = listUserOfOrg.stream().map(UserModel::getId).collect(Collectors.toList());
		if(!listOrgIsChoose.isEmpty()) {
			for(OrganizationModel organizationModel : listOrgIsChoose) {
				ApiGroupOrganizationExpandsModel apiGroupOrganizationExpandsModel = new ApiGroupOrganizationExpandsModel();
				apiGroupOrganizationExpandsModel.setName(organizationModel.getName());
				apiGroupOrganizationExpandsModel.setDescription(organizationModel.getDescription());
				if(!organizationModel.getUserOrganizationExpands().isEmpty()) {
					List<String> listIdUserOfThisOrg = organizationModel.getUserOrganizationExpands().stream().map(UserOrganizationExpandsModel::getUserId).collect(Collectors.toList());
					if(listIdUserOfThisOrg.equals(listIdUserInOrg)) {
						apiGroupOrganizationExpandsModel.setUserIds(listIdUserOfThisOrg);
					}else {
						ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Nhóm người dùng không thuộc đơn vị của bạn");
						confirmDialogTemplate.setText("Nhóm người dùng trong tổ chức "+organizationModel.getName()+" không thuộc đơn vị hiện tại. Bạn có muốn đưa nhóm người dùng này vào đơn vị hiện tại");
						confirmDialogTemplate.addCancelListener(e->{
							apiGroupOrganizationExpandsModel.setUserIds(new ArrayList<String>());
							doCreateGroup(apiGroupOrganizationExpandsModel);
						});
						
						confirmDialogTemplate.setCancelable(true);
						
						confirmDialogTemplate.addConfirmListener(e->{
							moveUserToOrg(listIdUserInOrg, listIdUserOfThisOrg);
							apiGroupOrganizationExpandsModel.setUserIds(listIdUserOfThisOrg);
							doCreateGroup(apiGroupOrganizationExpandsModel);
						});
						confirmDialogTemplate.open();
					}
				}else {
					apiGroupOrganizationExpandsModel.setUserIds(new ArrayList<String>());
					doCreateGroup(apiGroupOrganizationExpandsModel);
				}
				
			}
		}else {
			NotificationTemplate.error("Chưa chọn tổ chức");
		}
	}
	
	
	private int countOrg = 0;
	private void doCreateGroup(ApiGroupOrganizationExpandsModel apiGroupOrganizationExpandsModel) {
		try {
			ApiResultResponse<Object> createGroup = ApiOrganizationService.createGroup(orgId, apiGroupOrganizationExpandsModel);
			if(createGroup.getStatus()==200) {
				countOrg++;
				if(countOrg == listOrgIsChoose.size()) {
					fireEvent(new ClickEvent(this,false));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void moveUserToOrg(List<String> listIdUserInOrg,List<String> listIdUserInGroup) {
		try {
			ListUserIDsModel listUserIDsModel = new ListUserIDsModel();
			
			for(String id : listIdUserInOrg) {
				for(String idOther : listIdUserInGroup) {
					if(!idOther.equals(id)) {
						listUserIDsModel.getUserIds().add(idOther);
					}
				}
			}
			
			ApiResultResponse<Object> moveUser = ApiOrganizationService.moveUsersToOrg(orgId, listUserIDsModel);
			if(moveUser.getStatus()==200) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setOrg(OrganizationModel organizationModel) {
		this.organizationModel = organizationModel;
	}
	
	public OrganizationModel getOrg() {
		return this.organizationModel;
	}

}

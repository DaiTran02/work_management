package com.ngn.setting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.organization.ApiFilterOrgModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiOrganizationServiceCustom;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.sign_in_org.ApiSignInOrgModel;
import com.ngn.api.sign_in_org.ApiSignInOrgService;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.setting.components.ListRolesOfOrgForm;
import com.ngn.setting.components.ListUserOfOrgForm;
import com.ngn.setting.components.OrganizationNavForm;
import com.ngn.tdnv.dashboard.DashboardView;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@PageTitle(value = "Chọn đơn vị")
@Route(value = "first_login")
@PermitAll
public class ChooseOrgWhenTheFirstLogin extends DialogTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private final ApiOrganizationServiceCustom apiOrganizationServiceCustom;

	private VerticalLayout headerLayout = new VerticalLayout();
	private HorizontalLayout hFolderLayout = new HorizontalLayout();
	private OrganizationNavForm organizationNavForm = new OrganizationNavForm();
	private ButtonTemplate btnBackToOldOrg = new ButtonTemplate(FontAwesome.Solid.ARROW_LEFT.create());
	private H5	titleOrg = new H5();

	private ButtonTemplate btnUsers = new ButtonTemplate("Người dùng của đơn vị này");
	private Span spSplit = new Span("/");
	private ButtonTemplate btnRole = new ButtonTemplate("Vai trò của đơn vị này");
	private Span spSplit2 = new Span("/");
	private ButtonTemplate btnChoose = new ButtonTemplate("Chọn đơn vị này");
	
	private TextField txtSearch = new TextField();

	private VerticalLayout vLayout = new VerticalLayout();
	private Grid<ApiOrganizationModel> grid = new Grid<ApiOrganizationModel>(ApiOrganizationModel.class,false);
	private List<ApiOrganizationModel> listOrg = new ArrayList<ApiOrganizationModel>();


	private String idParent = null;
	public ChooseOrgWhenTheFirstLogin(ApiOrganizationServiceCustom apiOrganizationServiceCustom) {
		this.apiOrganizationServiceCustom = apiOrganizationServiceCustom;
		Pair<String, String> item = Pair.of(null,"Các tổ chức cấp cao nhất");
		organizationNavForm.addItem(item);
		buildLayout();
		loadData();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.setHeaderTitle("ĐĂNG NHẬP LẦN ĐẦU");
		this.getFooter().removeAll();

		H5 header = new H5("*Chọn một đơn vị hoạt động, lý do tài khoản này chưa có đơn vị sử dụng, chỉ được chọn một đơn vị hoạt động chính");

		titleOrg.getStyle().set("margin-top", "11px");

		btnBackToOldOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		hFolderLayout.getStyle().setBorderBottom("1px solid #a7d6ff");
		hFolderLayout.setWidthFull();
		hFolderLayout.expand(titleOrg);

		if(organizationNavForm.getCurrentItem().getLeft() == null) {
			titleOrg.setText("Danh sách các tổ chức");
			hFolderLayout.add(titleOrg);
		}

		btnUsers.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnUsers.getStyle().setMarginLeft("auto");

		btnRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnChoose.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		spSplit.getStyle().setMarginTop("10px").setColor("rgb(167, 214, 255)");
		spSplit2.getStyle().setMarginTop("10px").setColor("rgb(167, 214, 255)");

		txtSearch.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtSearch.setPlaceholder("Tìm kiếm đơn vị...");
		
		hFolderLayout.expand(txtSearch);

		headerLayout.setWidthFull();
		headerLayout.add(organizationNavForm,hFolderLayout);

		vLayout.setSizeFull();
		vLayout.add(header,new Hr(),headerLayout,createGrid());

		this.add(vLayout);
	}

	public void configComponent() {
		if(organizationNavForm != null) {
			organizationNavForm.addClickListener(e->{
				Pair<String, String> item = organizationNavForm.getCurrentItem();
				this.idParent = item.getLeft();
				loadData();
				if(item.getLeft() == null) {
					hFolderLayout.removeAll();
					titleOrg.setText("Danh sách các tổ chức");
					hFolderLayout.add(titleOrg);
				}else {
					hFolderLayout.removeAll();
					titleOrg.setText(item.getRight());
					hFolderLayout.add(btnBackToOldOrg,titleOrg,txtSearch,btnUsers,spSplit,btnRole,spSplit2,btnChoose);
				}
			});
		}

		if(organizationNavForm != null) {
			btnBackToOldOrg.addClickListener(e->{
				Pair<String, String> item = organizationNavForm.getItem();

				organizationNavForm.removeItem(item);
				organizationNavForm.buildLayout();

				this.idParent = item.getKey();
				loadData();

				if(item.getLeft() == null) {
					hFolderLayout.removeAll();

					titleOrg.setText("Danh sách tổ chức");

					hFolderLayout.add(titleOrg);
				}else {
					hFolderLayout.removeAll();

					titleOrg.setText(item.getValue());

					hFolderLayout.add(btnBackToOldOrg,titleOrg,txtSearch,btnUsers,spSplit,btnRole,spSplit2,btnChoose);

				}
			});
		}

		if(btnUsers != null)
			btnUsers.addClickListener(e->{
				if(organizationNavForm != null) {
					openDialogViewUser(organizationNavForm.getCurrentItem());
				}
			});
		
		if(btnRole != null)
			btnRole.addClickListener(e->{
				openDialogViewRoles(organizationNavForm.getCurrentItem());
			});
		
		if(txtSearch != null)
			txtSearch.addValueChangeListener(e->{
				loadData();
			});
		
		if(btnChoose != null)
			btnChoose.addClickListener(e->{
				openDialogConfirmAddOrg(organizationNavForm.getCurrentItem());
			});

	}

	public void loadData() {
		listOrg = new ArrayList<ApiOrganizationModel>();

		ApiFilterOrgModel apiFilterOrgModel = new ApiFilterOrgModel();
		apiFilterOrgModel.setParentId(idParent);
		apiFilterOrgModel.setSkip(0);
		apiFilterOrgModel.setLimit(0);
		apiFilterOrgModel.setKeyword(txtSearch.getValue());
		ApiResultResponse<List<ApiOrganizationModel>> data = apiOrganizationServiceCustom.getListOrg(apiFilterOrgModel);
		listOrg.addAll(data.getResult());

		grid.setItems(listOrg);
	}

	private Component createGrid() {
		grid = new Grid<ApiOrganizationModel>(ApiOrganizationModel.class,false);

		grid.addComponentColumn(model->{
			VerticalLayout vLayoutOrg = new VerticalLayout();
			ButtonTemplate btnOrg = new ButtonTemplate(model.getName());

			btnOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnOrg.addClickListener(e->{

				Pair<String, String> item = Pair.of(model.getId(),model.getName());

				organizationNavForm.addItem(item);

				hFolderLayout.removeAll();

				titleOrg.setText(model.getName());

				hFolderLayout.add(btnBackToOldOrg,titleOrg,txtSearch,btnUsers,spSplit,btnRole,spSplit2,btnChoose);

				btnBackToOldOrg.setTooltipText("Quay lại "+organizationNavForm.getItem().getValue());

				idParent = model.getId();
				loadData();
			});

			Span span = new Span("Có "+model.getCountSubOrganization()+ " đơn vị trực thuộc và "+model.getUserOrganizationExpands().size() + " người dùng");
			span.getStyle().set("font-style", "italic").setMarginLeft("10px");


			vLayoutOrg.add(btnOrg,span);
			vLayoutOrg.setSpacing(false);
			vLayoutOrg.setPadding(false);

			return vLayoutOrg;
		}).setHeader("Tên đơn vị");

		grid.addColumn(ApiOrganizationModel::getDescription).setHeader("Mô tả");
		
		grid.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();
			
			ButtonTemplate btnRole = new ButtonTemplate("Vai trò ("+model.getRoleOrganizationExpands().size()+")");
			btnRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnRole.addClickListener(e->{
				openDialogViewRoles(Pair.of(model.getId(),model.getName()));
			});
			
			ButtonTemplate btnUser = new ButtonTemplate("Người dùng ("+model.getUserOrganizationExpands().size()+")");
			btnUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnUser.addClickListener(e->{
				openDialogViewUser(Pair.of(model.getId(),model.getName()));
			});
			
			hLayout.add(btnRole,btnUser);
			
			return hLayout;
		}).setHeader("Thông tin");

		grid.addComponentColumn(model->{
			ButtonTemplate btnChoose = new ButtonTemplate("Chọn");
			
			btnChoose.addClickListener(e->{
				openDialogConfirmAddOrg(Pair.of(model.getId(),model.getName()));
			});

			return btnChoose;
		}).setHeader("Thao tác");

		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		return grid;
	}

	private void openDialogViewUser(Pair<String, String> item) {
		DialogTemplate dialogTemplate = new DialogTemplate("Danh sách người dùng thuộc đơn vị "+item.getValue());

		ListUserOfOrgForm listUserOfOrgForm = new ListUserOfOrgForm(item.getKey(), apiOrganizationServiceCustom);
		dialogTemplate.add(listUserOfOrgForm);
		
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeight("70%");

		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();
	}
	
	private void openDialogViewRoles(Pair<String, String> item) {
		DialogTemplate dialogTemplate = new DialogTemplate("Danh sách vai trò thuộc đơn vị "+item.getValue());
		
		ListRolesOfOrgForm listRolesOfOrgForm = new ListRolesOfOrgForm(item.getKey(),false, apiOrganizationServiceCustom);
		
		dialogTemplate.add(listRolesOfOrgForm);
		
		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeight("70%");
		dialogTemplate.getFooter().removeAll();
		
		dialogTemplate.open();
	}
	
	private void openDialogConfirmAddOrg(Pair<String, String> item) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chọn vai trò thuộc đơn vị "+item.getValue());
		ListRolesOfOrgForm listRolesOfOrgForm = new ListRolesOfOrgForm(item.getKey(), true, apiOrganizationServiceCustom);
		listRolesOfOrgForm.addChangeListener(e->{
			dialogTemplate.close();
			welcomeTo(item.getKey());
		});
		
		dialogTemplate.add(listRolesOfOrgForm);
		dialogTemplate.setWidth("70%");
		dialogTemplate.setHeight("90%");
		
		dialogTemplate.getBtnSave().setText("Xác nhận và chuyển vào sử dụng");
		dialogTemplate.getBtnSave().addClickListener(e->{
			listRolesOfOrgForm.addToOrg();
		});
		
		dialogTemplate.open();
	}
	
	private void welcomeTo(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> dataOrg = ApiOrganizationService.getOneOrg(idOrg);
		BelongOrganizationModel belongOrganizationModel = new BelongOrganizationModel();
		
		belongOrganizationModel.setOrganizationId(dataOrg.getResult().getId());
		belongOrganizationModel.setOrganizationName(dataOrg.getResult().getName());
		SessionUtil.setOrgId(belongOrganizationModel);
		
		SignInOrgModel signInOrgModel = getOrg(idOrg);
		SessionUtil.setDetailOrg(signInOrgModel);
		
		UI.getCurrent().navigate(DashboardView.class);
		this.close();
	}
	
	private SignInOrgModel getOrg(String id) {
		try {
			ApiResultResponse<ApiSignInOrgModel> getData = ApiSignInOrgService.getDetailOrg(id);
			SignInOrgModel signInOrgModel = new SignInOrgModel(getData.getResult());
			return signInOrgModel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

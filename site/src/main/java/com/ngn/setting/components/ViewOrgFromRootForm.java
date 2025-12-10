package com.ngn.setting.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.organization.ApiFilterOrgModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.sign_in_org.ApiSignInOrgModel;
import com.ngn.api.sign_in_org.ApiSignInOrgService;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.sign_in_org.SignInOrgModel;
import com.ngn.utils.CheckParametersUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.models.model_of_organization.OrganizationModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;

public class ViewOrgFromRootForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private VerticalLayout headerLayout = new VerticalLayout();
	private HorizontalLayout hFolderLayout = new HorizontalLayout();
	private OrganizationNavForm organizationNavForm = new OrganizationNavForm();
	private ButtonTemplate btnBackToOldOrg = new ButtonTemplate(FontAwesome.Solid.ARROW_LEFT.create());
	private H5	titleOrg = new H5();

	private ButtonTemplate btnUsers = new ButtonTemplate("Người dùng của đơn vị này");

	private TextField txtSearch = new TextField();

	private VerticalLayout vLayout = new VerticalLayout();
	private Grid<ApiOrganizationModel> grid = new Grid<ApiOrganizationModel>(ApiOrganizationModel.class,false);
	private List<ApiOrganizationModel> listOrg = new ArrayList<ApiOrganizationModel>();

	private ApiOrganizationModel orgIsChoose = null;

	private List<Checkbox> listCheckbox = new ArrayList<Checkbox>();
	private List<Pair<String,String>> listOrgToFindCurrentOrg = new ArrayList<Pair<String,String>>();

	private SignInOrgModel currentOrg = SessionUtil.getDetailOrg();


	private String idParent = null;


	public ViewOrgFromRootForm() {
		Pair<String, String> item = Pair.of(null,"Các tổ chức cấp cao nhất");
		organizationNavForm.addItem(item);
		buildLayout();
		if(currentOrg != null) {
			OrganizationModel org = getInfoOrg(currentOrg.getId());
			findCurrentOrg(org);
		}
		loadData();
		configComponent();
	}



	@Override
	public void buildLayout() {

		Span spanTitle = new Span("*Chọn đơn vị hoạt động, chỉ được chọn một đơn vị sử dụng, mỗi đơn vị sẽ có các nhóm và vai trò khác nhau.");
		spanTitle.getStyle().setFontWeight(600);
		this.add(spanTitle);

		btnBackToOldOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		hFolderLayout.getStyle().setBorderBottom("1px solid #a7d6ff").setDisplay(Display.FLEX).setAlignItems(AlignItems.CENTER);
		hFolderLayout.setWidthFull();
		hFolderLayout.expand(titleOrg);

		if(organizationNavForm.getCurrentItem().getLeft() == null) {
			titleOrg.setText("Danh sách các tổ chức");
			hFolderLayout.add(titleOrg);
		}

		btnUsers.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnUsers.getStyle().setMarginLeft("auto");



		txtSearch.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtSearch.setPlaceholder("Tìm kiếm đơn vị...");

		hFolderLayout.expand(txtSearch);

		headerLayout.setWidthFull();
		headerLayout.add(organizationNavForm,hFolderLayout);

		vLayout.setSizeFull();
		vLayout.add(headerLayout,createGrid());

		this.setSizeFull();

		this.add(vLayout);
	}

	@Override
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
					hFolderLayout.add(btnBackToOldOrg,titleOrg,txtSearch,btnUsers);
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

					hFolderLayout.add(btnBackToOldOrg,titleOrg,txtSearch,btnUsers);

				}
			});
		}

		if(btnUsers != null)
			btnUsers.addClickListener(e->{
				if(organizationNavForm != null) {
					openDialogViewUser(organizationNavForm.getCurrentItem());
				}
			});


		if(txtSearch != null)
			txtSearch.addValueChangeListener(e->{
				loadData();
			});

	}

	public void loadData() {
		listOrg = new ArrayList<ApiOrganizationModel>();
		
		ApiFilterOrgModel apiFilterOrgModel = new ApiFilterOrgModel();
		apiFilterOrgModel.setParentId(idParent);
		apiFilterOrgModel.setSkip(0);
		apiFilterOrgModel.setLimit(0);
		apiFilterOrgModel.setKeyword(txtSearch.getValue());
		ApiResultResponse<List<ApiOrganizationModel>> data = ApiOrganizationService.getListOrg(apiFilterOrgModel);
		if(data.isSuccess()) {
			listOrg.addAll(data.getResult());
		}

		grid.setItems(listOrg);
	}

	private void findCurrentOrg(OrganizationModel orgCurrent) {
		Pair<String, String> item2 = Pair.of(idParent,orgCurrent.getName()+" (đơn vị đang chọn)");

		listOrgToFindCurrentOrg.add(item2);
		if(orgCurrent.getParentId() != null) {
			findParent(orgCurrent.getParentId());
			idParent = orgCurrent.getParentId();
		}
		Collections.reverse(listOrgToFindCurrentOrg);
		listOrgToFindCurrentOrg.forEach(model->{
			organizationNavForm.addItem(model);
		});
	}

	private void findParent(String idParent) {
		ApiOrganizationModel apiOrganizationModel = getInforParentOrg(idParent);
		if(apiOrganizationModel != null) {
			listOrgToFindCurrentOrg.add(Pair.of(apiOrganizationModel.getId(),apiOrganizationModel.getName()));
			if(apiOrganizationModel.getParentId() != null) {
				findParent(apiOrganizationModel.getParentId());
			}
		}

	}

	private ApiOrganizationModel getInforParentOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> getOrg = ApiOrganizationService.getOneOrg(idOrg);
		if(getOrg.isSuccess()) {
			return getOrg.getResult();
		}
		return null;
	}


	private Component createGrid() {
		grid = new Grid<ApiOrganizationModel>(ApiOrganizationModel.class,false);

		grid.addComponentColumn(model->{
			Checkbox cbChoose = new Checkbox();

			if(currentOrg != null && currentOrg.getId().equals(model.getId())) {
				cbChoose.setValue(true);
			}

			cbChoose.addClickListener(e->{

				if(e.getSource().getValue()) {
					orgIsChoose = model;
				}else {
					orgIsChoose = null;
				}

				listCheckbox.forEach(cb->{
					if(!cb.equals(e.getSource())) {
						cb.setValue(false);
					}
				});

				fireEvent(new ClickEvent(this, false));
			});


			listCheckbox.add(cbChoose);

			return cbChoose;
		}).setWidth("50px").setFlexGrow(0);

		grid.addComponentColumn(model->{
			VerticalLayout vLayoutOrg = new VerticalLayout();
			ButtonTemplate btnOrg = new ButtonTemplate(model.getName());

			btnOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnOrg.addClickListener(e->{

				Pair<String, String> item = Pair.of(model.getId(),model.getName());

				organizationNavForm.addItem(item);

				hFolderLayout.removeAll();

				titleOrg.setText(model.getName());

				hFolderLayout.add(btnBackToOldOrg,titleOrg,txtSearch,btnUsers);

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

			ButtonTemplate btnUser = new ButtonTemplate("Người dùng ("+model.getUserOrganizationExpands().size()+")");
			btnUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnUser.addClickListener(e->{
				openDialogViewUser(Pair.of(model.getId(),model.getName()));
			});

			hLayout.add(btnUser);

			return hLayout;
		}).setHeader("Thông tin");


		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		return grid;
	}

	private void openDialogViewUser(Pair<String, String> item) {
		DialogTemplate dialogTemplate = new DialogTemplate("Danh sách người dùng thuộc đơn vị "+item.getValue());

		ListUserOfOrgForm listUserOfOrgForm = new ListUserOfOrgForm(item.getKey(), null);
		dialogTemplate.add(listUserOfOrgForm);

		dialogTemplate.setWidth("60%");
		dialogTemplate.setHeight("70%");

		dialogTemplate.getFooter().removeAll();

		dialogTemplate.open();
	}


	public boolean save() {
		CheckParametersUtil checkParametersUtil = new CheckParametersUtil(null);
		if(orgIsChoose != null) {
			SignInOrgModel signInOrgModel = getOrg(orgIsChoose.getId());
			if(signInOrgModel != null) {
				SessionUtil.setDetailOrg(signInOrgModel);
				BelongOrganizationModel belongOrganizationModel = new BelongOrganizationModel();
				belongOrganizationModel.setOrganizationId(orgIsChoose.getId());
				belongOrganizationModel.setOrganizationName(orgIsChoose.getName());

				SessionUtil.setOrgId(belongOrganizationModel);

				checkParametersUtil.handleParam();
				return true;
			}
		}else {
			NotificationTemplate.warning("Vui lòng chọn đơn vị");
		}
		return false;
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

	private OrganizationModel getInfoOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> getInfoOrg = ApiOrganizationService.getOneOrg(idOrg);
		if(getInfoOrg.isSuccess()) {
			return new OrganizationModel(getInfoOrg.getResult());
		}

		return null;
	}


	public ApiOrganizationModel getOrgIsChoose() {
		return orgIsChoose;
	}

}

package com.ngn.setting.step_setup_user;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.organization.ApiFilterOrgModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationServiceCustom;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.setting.components.ListRolesOfOrgForm;
import com.ngn.setting.components.ListUserOfOrgForm;
import com.ngn.setting.components.OrganizationNavForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
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

public class FirstStepChooseOrgForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout headerLayout = new VerticalLayout();
	private HorizontalLayout hFolderLayout = new HorizontalLayout();
	private OrganizationNavForm organizationNavForm = new OrganizationNavForm();
	private ButtonTemplate btnBackToOldOrg = new ButtonTemplate(FontAwesome.Solid.ARROW_LEFT.create());
	private H5	titleOrg = new H5();

	private ButtonTemplate btnUsers = new ButtonTemplate("Người dùng của đơn vị này");
	private Span spSplit = new Span("/");
	private ButtonTemplate btnRole = new ButtonTemplate("Vai trò của đơn vị này");
	
	private TextField txtSearch = new TextField();

	private VerticalLayout vLayout = new VerticalLayout();
	private Grid<ApiOrganizationModel> grid = new Grid<ApiOrganizationModel>(ApiOrganizationModel.class,false);
	private List<ApiOrganizationModel> listOrg = new ArrayList<ApiOrganizationModel>();
	
	private ApiOrganizationModel orgIsChoose = new ApiOrganizationModel();
	private ApiOrganizationModel orgOld = new ApiOrganizationModel();
	
	private List<Checkbox> listCheckbox = new ArrayList<Checkbox>();


	private String idParent = null;
	
	private ApiOrganizationServiceCustom apiOrganizationServiceCustom;
	
	public FirstStepChooseOrgForm() {
		Pair<String, String> item = Pair.of(null,"Các tổ chức cấp cao nhất");
		organizationNavForm.addItem(item);
		buildLayout();
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

		btnRole.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		spSplit.getStyle().setMarginTop("10px").setColor("rgb(167, 214, 255)");

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
					hFolderLayout.add(btnBackToOldOrg,titleOrg,txtSearch,btnUsers,spSplit,btnRole);
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

					hFolderLayout.add(btnBackToOldOrg,titleOrg,txtSearch,btnUsers,spSplit,btnRole);

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

	}
	
	public void setData(ApiOrganizationServiceCustom apiOrganizationServiceCustom) {
		this.apiOrganizationServiceCustom = apiOrganizationServiceCustom;
		loadData();
	}
	
	public void setOrg(ApiOrganizationModel orgOld) {
		this.orgOld = orgOld;
		loadData();
	}

	public void loadData() {
		listOrg = new ArrayList<ApiOrganizationModel>();

		ApiFilterOrgModel apiFilterOrgModel = new ApiFilterOrgModel();
		apiFilterOrgModel.setParentId(idParent);
		apiFilterOrgModel.setSkip(0);
		apiFilterOrgModel.setLimit(0);
		apiFilterOrgModel.setKeyword(txtSearch.getValue());
		if(apiOrganizationServiceCustom != null) {
			ApiResultResponse<List<ApiOrganizationModel>> data = apiOrganizationServiceCustom.getListOrg(apiFilterOrgModel);
			if(data.isSuccess()) {
				listOrg.addAll(data.getResult());
			}
		}
		

		grid.setItems(listOrg);
	}
	

	private Component createGrid() {
		grid = new Grid<ApiOrganizationModel>(ApiOrganizationModel.class,false);
		
		grid.addComponentColumn(model->{
			Checkbox cbChoose = new Checkbox();
			
			if(orgOld != null && model.getId().equals(orgOld.getId())) {
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

				hFolderLayout.add(btnBackToOldOrg,titleOrg,txtSearch,btnUsers,spSplit,btnRole);

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
	

	public ApiOrganizationModel getOrgIsChoose() {
		return orgIsChoose;
	}
	
}

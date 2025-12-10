package com.ngn.tdnv.doc.forms.create_task.level_pass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.organization.ApiCategoriesOrgModel;
import com.ngn.api.organization.ApiFilterOrgModel;
import com.ngn.api.organization.ApiOrganizationModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.setting.components.OrganizationNavForm;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.models.model_of_organization.OrganizationModel;
import com.ngn.utils.models.model_of_organization.UserOranizationExModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.dom.Style.AlignItems;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexWrap;

//Class dùng cho việc chọn nơi nhận của văn bản
public class TaskChooseAllOrgForReceiversForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean isUiMobile = false;

	//Header 
	private VerticalLayout headerLayout = new VerticalLayout();
	private HorizontalLayout hFolderLayout = new HorizontalLayout();
	private OrganizationNavForm organizationNavForm = new OrganizationNavForm();
	private ButtonTemplate btnBackToOldOrg = new ButtonTemplate(FontAwesome.Solid.ARROW_LEFT.create());
	private H5	titleOrg = new H5();

	//	private ButtonTemplate btnUsers = new ButtonTemplate("Người dùng của đơn vị này");
	//	private Span spSplit = new Span("/");
	//	private ButtonTemplate btnRole = new ButtonTemplate("Vai trò của đơn vị này");

	private HorizontalLayout hLayoutGeneral = new HorizontalLayout();
	private Checkbox cbAll = new Checkbox();
	private TextField txtSearch = new TextField();
	private ComboBox<Pair<String, String>> cmbTypeOrg = new ComboBox<Pair<String,String>>();
	private ButtonTemplate btnSearch = new ButtonTemplate(FontAwesome.Solid.SEARCH.create());

	private Grid<OrganizationModel> grid = new Grid<OrganizationModel>();
	@SuppressWarnings("unused")
	private String withOfFieldUserInGrid = "120px";
	private String withOfFieldOrgInGrid = "60%";
	private List<OrganizationModel> listModel = new ArrayList<OrganizationModel>();
	private Div hLayoutSelected = new Div();

	private List<OrganizationModel> listOrgIsChoose = new ArrayList<OrganizationModel>();
	private TaskChooseUserForOrgForm taskChooseUserForOrgForm = new TaskChooseUserForOrgForm();

	private Map<OrganizationModel, UserOranizationExModel> mapOrgIsChoose = new HashMap<OrganizationModel, UserOranizationExModel>();

	private Map<OrganizationModel, UserOranizationExModel> mapUserOfOrg = new HashMap<OrganizationModel, UserOranizationExModel>();

	private List<Checkbox> listCheckBox = new ArrayList<Checkbox>();
	private List<Pair<String,String>> listOrgToFindCurrentOrg = new ArrayList<Pair<String,String>>();

	private String idParent;
	private Map<OrganizationModel, UserOranizationExModel> mapOrgAssigneeWasChoose;
	private boolean checkSupport = false;
	private boolean checkUpdate = false;
	private OrganizationModel ownerOrg;
	public TaskChooseAllOrgForReceiversForm(String idParent,OrganizationModel ownerOrg,Map<OrganizationModel, UserOranizationExModel> mapOrgAssigneeWasChoose,boolean checkUpdate) {
		this.idParent = idParent;
		this.ownerOrg = ownerOrg;
		this.mapOrgAssigneeWasChoose = mapOrgAssigneeWasChoose;
		this.checkUpdate = checkUpdate;

		Pair<String, String> item1 = Pair.of(null,"Các tổ chức cao nhất");
		organizationNavForm.addItem(item1);

		Pair<String, String> item2 = Pair.of(idParent,this.ownerOrg.getName());
		organizationNavForm.addItem(item2);

		checkUiMobile();
		buildLayout();
		configComponent();
		loadOrgType();
		findCurrentOrg();
		loadData();

	}


	@Override
	public void buildLayout() {
		this.setSizeFull();
		hLayoutSelected.setVisible(false);

		btnBackToOldOrg.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		hFolderLayout.getStyle().setBorderBottom("1px solid #a7d6ff").setDisplay(Display.FLEX).setAlignItems(AlignItems.CENTER);
		hFolderLayout.setWidthFull();
		hFolderLayout.expand(titleOrg);

		if(organizationNavForm.getCurrentItem().getLeft() == null) {
			titleOrg.setText("Danh sách các tổ chức");
			hFolderLayout.add(titleOrg);
		}


		cmbTypeOrg.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

		txtSearch.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtSearch.setPlaceholder("Tìm kiếm đơn vị...");


		headerLayout.setWidthFull();
		headerLayout.add(organizationNavForm,hFolderLayout);

		this.add(headerLayout);

		VerticalLayout vLayoutContentOrg = new VerticalLayout();
		vLayoutContentOrg.add(createToolbar(),createGrid());
		vLayoutContentOrg.setSizeFull();

		hLayoutGeneral.setFlexGrow(2, vLayoutContentOrg);
		hLayoutGeneral.setFlexGrow(1, taskChooseUserForOrgForm);
		hLayoutGeneral.add(vLayoutContentOrg,taskChooseUserForOrgForm);
		taskChooseUserForOrgForm.setWidth("50em");
		hLayoutGeneral.setSizeFull();

		taskChooseUserForOrgForm.setVisible(false);

		this.add(hLayoutGeneral,hLayoutSelected);

	}

	@Override
	public void configComponent() {
		cbAll.addClickListener(e->{
			if(cbAll.getValue() == true) {
				listCheckBox.forEach(cb->{
					if(!cb.getValue() == true) {
						listModel.forEach(org->{
							if(cb.getId().get().equals(org.getId())) {
								mapOrgIsChoose.putIfAbsent(org, null);
							}
						});
						cb.setValue(true);
					}
				});
			}else {
				listCheckBox.forEach(cb->{
					cb.setValue(false);
				});
				mapOrgIsChoose.clear();

				if(!mapUserOfOrg.isEmpty()) {
					listCheckBox.forEach(cb->{
						mapUserOfOrg.forEach((k,v)->{
							if(k.getId().equals(cb.getId().get())) {
								cb.setValue(true);
								mapOrgIsChoose.putIfAbsent(k, v);
							}
						});
					});
				}
			}
			showOrgSelect();
		});


		btnSearch.addClickListener(e->{
			loadData();
		});

		txtSearch.addValueChangeListener(e->loadData());
		cmbTypeOrg.addValueChangeListener(e->loadData());

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
					hFolderLayout.add(btnBackToOldOrg,titleOrg);
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

					hFolderLayout.add(btnBackToOldOrg,titleOrg);

				}
			});
		}
	}

	private void loadData() {
		listModel = new ArrayList<OrganizationModel>();
		listCheckBox = new ArrayList<Checkbox>();
		try {
			//			listModel.add(ownerOrg);
			ApiResultResponse<List<ApiOrganizationModel>> data = ApiOrganizationService.getListOrg(getSearch());
			//			listModel = data.getResult().stream().map(OrganizationModel::new).toList();
			data.getResult().stream().forEach(model->{
				listModel.add(new OrganizationModel(model));
			});
			listModel = new ArrayList<OrganizationModel>(listModel);
			if(checkSupport) {
				mapOrgAssigneeWasChoose.forEach((k,v)->{
					listModel.removeIf(mem->mem.getId().equals(k.getId()));
				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		grid.setItems(listModel);
	}
	
	private void findCurrentOrg() {
		Pair<String, String> item2 = Pair.of(idParent,this.ownerOrg.getName()+" (đơn vị đang giao nhiệm vụ)");
		
		listOrgToFindCurrentOrg.add(item2);
		if(ownerOrg.getParentId() != null) {
			findParent(ownerOrg.getParentId());
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

	private void checkUiMobile() {
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					isUiMobile = true;
					withOfFieldUserInGrid = "60px";
					withOfFieldOrgInGrid = "200px";
				}
			});
		} catch (Exception e) {
		}
	}

	private Component createToolbar() {
		VerticalLayout vLayoutFilter = new VerticalLayout();

		Span spNote = new Span("*Ghi chú: Nếu chọn nhiều đơn vị thực hiện, nhiệm vụ sẽ được chuyển thành giao nhiệm vụ cho nhiều đơn vị "
				+ "và sẽ thể chọn thêm đơn vị phối hợp (nhấp vào tên đơn vị để xem các đơn vị trực thuộc)");

		vLayoutFilter.add(spNote);

		HorizontalLayout hLayoutFilter = new HorizontalLayout();
		hLayoutFilter.setWidthFull();
		hLayoutFilter.add(cbAll,txtSearch,cmbTypeOrg,btnSearch);

		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Nhập tên để tìm đơn vị...");

		cbAll.getStyle().setMarginTop("5px").setMarginLeft("7px");

		cmbTypeOrg.setWidth("300px");


		if(isUiMobile) {
			hLayoutFilter.getStyle().setDisplay(Display.FLEX).setFlexWrap(FlexWrap.WRAP);
			hLayoutFilter.removeAll();
			hLayoutFilter.add(cmbTypeOrg,btnSearch,cbAll,txtSearch);
			txtSearch.setWidth("85%");
			cmbTypeOrg.setWidth("85%");
		}

		vLayoutFilter.add(hLayoutFilter);

		vLayoutFilter.setWidthFull();
		return vLayoutFilter;
	}


	private Component createGrid() {
		grid = new Grid<OrganizationModel>();
		grid.setSelectionMode(SelectionMode.SINGLE);
		grid.addComponentColumn(model->{
			Checkbox cbChooseOrg = new Checkbox();
			cbChooseOrg.setId(model.getId());


			cbChooseOrg.addClickListener(e->{
				if(cbChooseOrg.getValue()) {
					mapOrgIsChoose.putIfAbsent(model, null);
				}else {
					mapOrgIsChoose.remove(model);
					if(checkUpdate) {
						for(Map.Entry<OrganizationModel, UserOranizationExModel> m : mapOrgIsChoose.entrySet()) {
							if(m.getKey().getId().equals(model.getId())) {
								mapOrgIsChoose.remove(m.getKey());
								break;
							}
						}
					}
				}

//				if(checkUpdate && checkSupport == false) {
//					listCheckBox.forEach(cb->{
//						if(e.getSource().equals(cb)) {
//							mapOrgIsChoose.clear();
//							if(cbChooseOrg.getValue()) {
//								mapOrgIsChoose.put(model, null);
//							}
//						}else {
//							cb.setValue(false);
//						}
//					});
//				}
				showOrgSelect();
			});


			if(!mapOrgAssigneeWasChoose.isEmpty() && checkSupport == false) {
				mapOrgAssigneeWasChoose.forEach((k,v)->{
					if(k.getId().equals(model.getId())) {
						cbChooseOrg.setValue(true);
						if(mapOrgAssigneeWasChoose.get(k) != null ) {
							mapUserOfOrg.put(model, v);
						}
					}
				});
				mapOrgIsChoose.putAll(mapOrgAssigneeWasChoose);
				mapOrgAssigneeWasChoose.clear();
			}


			if(!mapUserOfOrg.isEmpty()) {
				mapUserOfOrg.forEach((k,v)->{
					if(model.equals(k)) {
						cbChooseOrg.setValue(true);
						cbChooseOrg.setReadOnly(true);
						mapOrgIsChoose.put(k, v);
					}
				});


			}

			if(!mapOrgIsChoose.isEmpty()) {
				mapOrgIsChoose.forEach((k,v)->{
					if(k.getId().equals(model.getId())) {
						cbChooseOrg.setValue(true);
					}
				});
				showOrgSelect();
			}

			listCheckBox.add(cbChooseOrg);

			return cbChooseOrg;
		}).setWidth("50px").setFlexGrow(0);

		grid.addComponentColumn(model->{
			VerticalLayout vLayoutOrg = new VerticalLayout();


			ButtonTemplate btnHeader = new ButtonTemplate(model.getName());
			btnHeader.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

			btnHeader.addClickListener(e->{

				Pair<String, String> item = Pair.of(model.getId(),model.getName());

				organizationNavForm.addItem(item);

				hFolderLayout.removeAll();

				titleOrg.setText(model.getName());

				hFolderLayout.add(btnBackToOldOrg,titleOrg);

				btnBackToOldOrg.setTooltipText("Quay lại "+organizationNavForm.getItem().getValue());

				idParent = model.getId();
				loadData();
			});

			Span spDers = new Span(model.getCountSubOrganization() + " đơn vị trực thuộc / Mô tả: "+model.getDescription());
			spDers.getStyle().setColor("hsl(220 min(calc(35% * .8), 35%) 16% / .98)").set("font-style", "italic");

			vLayoutOrg.add(btnHeader,spDers);
			vLayoutOrg.setSpacing(false);


			if(isUiMobile) {
				HorizontalLayout hLayoutUserChoose = new HorizontalLayout();
				TextField txtUser = new TextField();
				txtUser.setReadOnly(true);
				txtUser.setWidth("150px");

				ButtonTemplate btnRemove = new ButtonTemplate(FontAwesome.Solid.REMOVE.create());
				btnRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR);
				btnRemove.setTooltipText("Bỏ chọn người dùng");
				btnRemove.addClickListener(e->{
					if(mapUserOfOrg.containsKey(model)) {
						mapUserOfOrg.remove(model);
						mapOrgAssigneeWasChoose.replace(model, null);
						loadData();
					}
				});

				hLayoutUserChoose.add(txtUser,btnRemove);
				hLayoutUserChoose.setVisible(false);

				if(mapUserOfOrg.containsKey(model)) {
					txtUser.clear();
					txtUser.setValue(mapUserOfOrg.get(model).getFullName());
					hLayoutUserChoose.setVisible(true);
				}else {
					txtUser.clear();
					hLayoutUserChoose.setVisible(false);
				}

				vLayoutOrg.add(hLayoutUserChoose);
			}

			return vLayoutOrg;
		}).setResizable(true).setWidth(withOfFieldOrgInGrid).setFlexGrow(0);
//		grid.addComponentColumn(model->{
//			ButtonTemplate btnChooseUser = new ButtonTemplate("Người dùng ("+model.getUserOrganizationExpands().size()+")");
//			btnChooseUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//			btnChooseUser.addClickListener(e->{
//				openFormUser(model,model.getUserOrganizationExpands());
//			});
//
//
//			if(isUiMobile) {
//				btnChooseUser.setText("("+model.getUserOrganizationExpands().size()+")");
//				btnChooseUser.setIcon(FontAwesome.Solid.USER.create());
//				btnChooseUser.getStyle().setFontSize("13px");
//			}
//
//			return btnChooseUser;
//		}).setResizable(true).setWidth(withOfFieldUserInGrid).setFlexGrow(0);

//		if(!isUiMobile) {
//			grid.addComponentColumn(model->{
//				HorizontalLayout hLayoutUserChoose = new HorizontalLayout();
//
//				TextField txtUser = new TextField();
//				txtUser.setReadOnly(true);
//
//				ButtonTemplate btnRemove = new ButtonTemplate(FontAwesome.Solid.REMOVE.create());
//				btnRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//				btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR);
//				btnRemove.setTooltipText("Bỏ chọn người dùng");
//				btnRemove.addClickListener(e->{
//					if(mapUserOfOrg.containsKey(model)) {
//						mapUserOfOrg.remove(model);
//						mapOrgAssigneeWasChoose.replace(model, null);
//						loadData();
//					}
//				});
//
//				hLayoutUserChoose.add(txtUser,btnRemove);
//				hLayoutUserChoose.setVisible(false);
//
//				if(mapUserOfOrg.containsKey(model)) {
//					txtUser.clear();
//					txtUser.setValue(mapUserOfOrg.get(model).getFullName());
//					hLayoutUserChoose.setVisible(true);
//				}else {
//					txtUser.clear();
//					hLayoutUserChoose.setVisible(false);
//				}
//
//				return hLayoutUserChoose;
//			}).setResizable(true);
//		}

		grid.getStyle().setPadding("0");

		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		return grid;
	}

//	private void openFormUser(OrganizationModel organizationModel,List<UserOranizationExModel> listUser) {
//		taskChooseUserForOrgForm.setVisible(true);
//		taskChooseUserForOrgForm.loadData(organizationModel,mapUserOfOrg);
//		taskChooseUserForOrgForm.getBtnClose().addClickListener(e->{
//			closeFormUser();
//		});
//
//		taskChooseUserForOrgForm.addChangeListener(e->{
//			if(checkUpdate && checkSupport == false) {
//				mapUserOfOrg.clear();
//				mapOrgIsChoose.clear();
//			}
//			mapUserOfOrg.putAll(taskChooseUserForOrgForm.getMapUserIsChoose());
//			if(!mapOrgAssigneeWasChoose.isEmpty()) {
//				taskChooseUserForOrgForm.getMapUserIsChoose().forEach((k,v)->{
//					if(mapOrgAssigneeWasChoose.containsKey(k)) {
//						mapOrgAssigneeWasChoose.replace(k, v);
//					}
//				});
//			}
//
//			loadData();
//		});
//	}

	private void clearAll() {
		mapUserOfOrg.clear();
		listOrgIsChoose.clear();
		mapOrgIsChoose.clear();
		mapOrgAssigneeWasChoose.clear();
		loadData();
		showOrgSelect();
	}

	private void closeFormUser() {
		taskChooseUserForOrgForm.setVisible(false);
	}

	private boolean isMinizime = false;
	private void showOrgSelect() {
		this.add(hLayoutSelected);
		hLayoutSelected.removeAll();
		//This class in file task.css in line 164
		hLayoutSelected.addClassName("layout-ord-selected");
		hLayoutSelected.setVisible(true);
		if(isUiMobile) {
			hLayoutSelected.getStyle().set("left", "15%");
		}

		Span spOrg = new Span(mapOrgIsChoose.size()+" đơn vị đã chọn");
		spOrg.getStyle().setColor("white");

		ButtonTemplate btnSave = new ButtonTemplate("Xác nhận");
		//		if(!mapOrgAssigneeWasChoose.isEmpty()) {
		//			btnSave.setText("Cập nhật");
		//		}
		btnSave.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSave.getStyle().setBorder("1px solid hsl(220 calc(22% / 2) 67% / .18)").setColor("rgb(255 255 255)");
		btnSave.setTooltipText("Xác nhận chọn những đơn vị này");
		btnSave.addClickListener(e->{
			fireEvent(new ClickEvent(this,false));
		});

		ButtonTemplate btnRemoveAll = new ButtonTemplate(FontAwesome.Solid.TRASH.create());
		btnRemoveAll.setTooltipText("Bỏ chọn tất cả");
		btnRemoveAll.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnRemoveAll.addThemeVariants(ButtonVariant.LUMO_ERROR);
		btnRemoveAll.getStyle().setBorder("1px solid hsl(220 calc(22% / 2) 67% / .18)");
		btnRemoveAll.addClickListener(e->{
			openDialogConfirmClear();
		});

		ButtonTemplate btnMinimize = new ButtonTemplate(FontAwesome.Solid.MINUS.create());
		btnMinimize.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnMinimize.getStyle().setColor("white");



		ButtonTemplate btnRestoreLayout = new ButtonTemplate(FontAwesome.Solid.PLUS.create());
		btnRestoreLayout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnRestoreLayout.getStyle().setColor("white");


		btnMinimize.addClickListener(e->{
			hLayoutSelected.removeAll();
			hLayoutSelected.setWidth("30px");
			hLayoutSelected.getStyle().setBorderRadius("50%");
			hLayoutSelected.getStyle().set("transition", "all 0.3s ease-in-out");
			if(isUiMobile) {
				hLayoutSelected.getStyle().set("left", "85%");
			}else {
				hLayoutSelected.getStyle().set("left", "95%");
			}

			isMinizime = true;

			hLayoutSelected.add(btnRestoreLayout);
		});

		btnRestoreLayout.addClickListener(e->{
			hLayoutSelected.removeAll();
			hLayoutSelected.setWidth("245px");
			hLayoutSelected.getStyle().set("border-radius", "10px");
			hLayoutSelected.getStyle().set("transition", "all 0.3s ease-in-out");
			hLayoutSelected.removeClassName("layout-ord-selected");
			hLayoutSelected.addClassName("layout-ord-selected");
			if(isUiMobile) {
				hLayoutSelected.getStyle().set("left", "15%");
			}else {
				hLayoutSelected.getStyle().set("left", "45%");
			}
			isMinizime = false;
			hLayoutSelected.add(spOrg,btnSave,btnRemoveAll,btnMinimize);
		});


		if(isMinizime) {
			if(isUiMobile) {
				hLayoutSelected.getStyle().set("left", "85%");
			}else {
				hLayoutSelected.getStyle().set("left", "95%");
			}
			hLayoutSelected.add(btnRestoreLayout);
		}else {
			hLayoutSelected.add(spOrg,btnSave,btnRemoveAll,btnMinimize);
		}
	}

	private void openDialogConfirmClear() {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("Bỏ chọn tất cả");

		confirmDialogTemplate.setText("Xác nhận bỏ chọn tất cả");
		confirmDialogTemplate.getBtnConfirm().addClickListener(e->{
			clearAll();
			closeFormUser();
			cbAll.setValue(false);
		});

		confirmDialogTemplate.open();
	}

	private ApiFilterOrgModel getSearch() {
		ApiFilterOrgModel apiFilterOrgModel = new ApiFilterOrgModel();

		apiFilterOrgModel.setParentId(idParent);
		apiFilterOrgModel.setKeyword(txtSearch.getValue());
		apiFilterOrgModel.setOrganizationCategoryId(cmbTypeOrg.getValue().getKey());

		return apiFilterOrgModel;
	}

	private void loadOrgType() {
		List<Pair<String, String>> listOrgCate = new ArrayList<Pair<String,String>>();
		listOrgCate.add(Pair.of(null,"Tất cả"));
		ApiResultResponse<List<ApiCategoriesOrgModel>> data = ApiOrganizationService.getListOrgCategories();
		if(data.isSuccess()) {
			data.getResult().stream().forEach(model->{
				listOrgCate.add(Pair.of(model.getId(),model.getName()));
			});
		}

		cmbTypeOrg.setItems(listOrgCate);
		cmbTypeOrg.setItemLabelGenerator(Pair::getValue);
		cmbTypeOrg.setValue(listOrgCate.get(0));
	}


	public Map<OrganizationModel, UserOranizationExModel> getMapOrgIsChoose() {
		return mapOrgIsChoose;
	}

}

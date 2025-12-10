package com.ngn.tdnv.doc.forms.create_task;

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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexWrap;

public class TaskChooseFollowerByOrgForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private boolean isUiMobile = false;

	private HorizontalLayout hLayoutGeneral = new HorizontalLayout();
	private Checkbox cbAll = new Checkbox();
	private TextField txtSearch = new TextField();
	private ComboBox<Pair<String, String>> cmbTypeOrg = new ComboBox<Pair<String,String>>();
	private ButtonTemplate btnSearch = new ButtonTemplate(FontAwesome.Solid.SEARCH.create());

	private Grid<OrganizationModel> grid = new Grid<OrganizationModel>();
	private String withOfFieldUserInGrid = "120px";
	private String withOfFieldOrgInGrid = "60%";
	private List<OrganizationModel> listModel = new ArrayList<OrganizationModel>();
	private Div hLayoutSelected = new Div();

	private List<OrganizationModel> listOrgIsChoose = new ArrayList<OrganizationModel>();
	private TaskChooseUserFollowerOfOrgForm taskChooseUserFollowerOfOrgForm = new TaskChooseUserFollowerOfOrgForm();

	private Map<OrganizationModel, UserOranizationExModel> mapOrgIsChoose = new HashMap<OrganizationModel, UserOranizationExModel>();

	private Map<OrganizationModel, UserOranizationExModel> mapUserOfOrg = new HashMap<OrganizationModel, UserOranizationExModel>();

	private List<Checkbox> listCheckBox = new ArrayList<Checkbox>();

	private String idParent;
	private Map<OrganizationModel, UserOranizationExModel> mapOrgFollowerWasChoose;
	private boolean checkUpdate = false;
	@SuppressWarnings("unused")
	private OrganizationModel ownerOrg;

	public TaskChooseFollowerByOrgForm(String idParent,OrganizationModel ownerOrg,Map<OrganizationModel, UserOranizationExModel> mapOrgFollowerChoose,boolean checkUpdate) {
		this.idParent = idParent;
		this.ownerOrg = ownerOrg;
		this.mapOrgFollowerWasChoose = mapOrgFollowerChoose;
		this.checkUpdate = checkUpdate;
		checkUiMobile();
		buildLayout();
		configComponent();
		loadOrgType();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		hLayoutSelected.setVisible(false);

		VerticalLayout vLayoutContentOrg = new VerticalLayout();
		vLayoutContentOrg.add(createToolbar(),createGrid());
		vLayoutContentOrg.setSizeFull();

		hLayoutGeneral.setFlexGrow(2, vLayoutContentOrg);
		hLayoutGeneral.setFlexGrow(1, taskChooseUserFollowerOfOrgForm);
		hLayoutGeneral.add(vLayoutContentOrg,taskChooseUserFollowerOfOrgForm);
		taskChooseUserFollowerOfOrgForm.setWidth("50em");
		hLayoutGeneral.setSizeFull();

		taskChooseUserFollowerOfOrgForm.setVisible(false);

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
	}

	private void loadData() {
		listModel = new ArrayList<OrganizationModel>();
		listCheckBox = new ArrayList<Checkbox>();
		try {
//			listModel.add(ownerOrg);
//			ApiResultResponse<List<ApiOrganizationModel>> data = ApiOrganizationService.getListOrg(getSearch());
//			//			listModel = data.getResult().stream().map(OrganizationModel::new).toList();
//			data.getResult().stream().forEach(model->{
//				listModel.add(new OrganizationModel(model));
//			});
			
			ApiOrganizationModel apiOrganizationModel = getOrg(idParent);
			if(apiOrganizationModel != null) {
				if(apiOrganizationModel.getLevel().getKey().equals("room")) {
					ApiOrganizationModel parentOrg = getOrg(apiOrganizationModel.getParentId());
					OrganizationModel prOrg = new OrganizationModel(parentOrg);
					listModel.add(prOrg);
					List<OrganizationModel> listSubOrg = getListSubOrg(parentOrg.getId());
					if(listSubOrg != null) {
						listModel.addAll(listSubOrg);
					}
				}else {
					List<OrganizationModel> listSubOrg = getListSubOrg(idParent);
					
					OrganizationModel currentOrg = new OrganizationModel(getOrg(idParent));
					listSubOrg.add(currentOrg);
					if(listSubOrg != null) {
						listModel.addAll(listSubOrg);
					}
					Collections.reverse(listModel);
				}
			}
			listModel = new ArrayList<OrganizationModel>(listModel);

		} catch (Exception e) {
			e.printStackTrace();
		}
		grid.setItems(listModel);
	}
	
	private ApiOrganizationModel getOrg(String idOrg) {
		ApiResultResponse<ApiOrganizationModel> data = ApiOrganizationService.getOneOrg(idOrg);
		if(data.isSuccess()) {
			return data.getResult();
		}
		return null;
	}
	
	private List<OrganizationModel> getListSubOrg(String idOrg){
		ApiFilterOrgModel apiFilterOrgModel = getSearch();
		apiFilterOrgModel.setLimit(0);
		apiFilterOrgModel.setSkip(0);
		apiFilterOrgModel.setParentId(idOrg);
		ApiResultResponse<List<ApiOrganizationModel>> data = ApiOrganizationService.getListOrg(apiFilterOrgModel);
		if(data.isSuccess()) {
			List<OrganizationModel> listData = new ArrayList<OrganizationModel>();
			data.getResult().forEach(model->{
				listData.add(new OrganizationModel(model));
			});
			return listData;
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

		Span spNote = new Span("*Ghi chú: Chọn các đơn vị theo dõi nhiệm vụ này");
		spNote.getStyle().setFontWeight(600);

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
				}
				showOrgSelect();
			});

			if(!mapOrgFollowerWasChoose.isEmpty()) {
				//				if(mapOrgFollowerChoose.containsKey(model)) {
				//					cbChooseOrg.setValue(true);
				//					if(mapOrgFollowerChoose.get(model) != null) {
				//						mapUserOfOrg.put(model, mapOrgFollowerChoose.get(model));
				//					}
				//				}
				
				mapOrgFollowerWasChoose.forEach((k,v)->{
					if(k.getId().equals(model.getId())) {
						mapOrgIsChoose.put(model, mapOrgFollowerWasChoose.get(k));
						if(v != null) {
							mapUserOfOrg.put(model, mapOrgFollowerWasChoose.get(k));
						}
					}
				});
				
//				if(mapOrgFollowerChoose.containsKey(model)) {
//					mapOrgIsChoose.put(model, mapOrgFollowerChoose.get(model));
//				}
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
				if(mapOrgIsChoose.containsKey(model)) {
					cbChooseOrg.setValue(true);
				}
				if(checkUpdate) {
					mapOrgIsChoose.forEach((k,v)->{
						if(k.getId().equals(model.getId())) {
							cbChooseOrg.setValue(true);
						}
					});
				}
				showOrgSelect();
			}


			listCheckBox.add(cbChooseOrg);

			return cbChooseOrg;
		}).setWidth("50px").setFlexGrow(0);

		grid.addComponentColumn(model->{
			VerticalLayout vLayoutOrg = new VerticalLayout();

			String textOwner = idParent.equals(model.getId()) ? " (đơn vị đang đang giao nhiệm vụ)" : "";

			H5 header = new H5(model.getName()+textOwner);

			Span spDers = new Span(model.getDescription());
			spDers.getStyle().setColor("hsl(220 min(calc(35% * .8), 35%) 16% / .98)").set("font-style", "italic");

			vLayoutOrg.add(header,spDers);


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
						mapOrgFollowerWasChoose.replace(model, null);
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
		grid.addComponentColumn(model->{
			ButtonTemplate btnChooseUser = new ButtonTemplate("Người dùng ("+model.getUserOrganizationExpands().size()+")");
			btnChooseUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnChooseUser.addClickListener(e->{
				openFormUser(model,model.getUserOrganizationExpands());
			});


			if(isUiMobile) {
				btnChooseUser.setText("("+model.getUserOrganizationExpands().size()+")");
				btnChooseUser.setIcon(FontAwesome.Solid.USER.create());
				btnChooseUser.getStyle().setFontSize("13px");
			}

			return btnChooseUser;
		}).setResizable(true).setWidth(withOfFieldUserInGrid).setFlexGrow(0);

		if(!isUiMobile) {
			grid.addComponentColumn(model->{
				HorizontalLayout hLayoutUserChoose = new HorizontalLayout();

				TextField txtUser = new TextField();
				txtUser.setReadOnly(true);

				ButtonTemplate btnRemove = new ButtonTemplate(FontAwesome.Solid.REMOVE.create());
				btnRemove.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				btnRemove.addThemeVariants(ButtonVariant.LUMO_ERROR);
				btnRemove.setTooltipText("Bỏ chọn người dùng");
				btnRemove.addClickListener(e->{
					if(mapUserOfOrg.containsKey(model)) {
						mapUserOfOrg.remove(model);
						mapOrgFollowerWasChoose.forEach((k,v)->{
							if(k.getId().equals(model.getId())) {
								mapOrgFollowerWasChoose.replace(k, null);
							}
							
						});
						loadData();
					}
				});

				hLayoutUserChoose.add(txtUser,btnRemove);
				hLayoutUserChoose.setVisible(false);

				//				if(mapUserOfOrg.get(model) != null) {
				//					if(mapUserOfOrg.containsKey(model)) {
				//						txtUser.clear();
				//						txtUser.setValue(mapUserOfOrg.get(model).getUserName() == null ? "" : mapUserOfOrg.get(model).getFullName());
				//						hLayoutUserChoose.setVisible(true);
				//					}else {
				//						txtUser.clear();
				//						hLayoutUserChoose.setVisible(false);
				//					}
				//				}else {
				//					txtUser.clear();
				//					hLayoutUserChoose.setVisible(false);
				//				}
				txtUser.clear();
				hLayoutUserChoose.setVisible(false);
				mapUserOfOrg.forEach((k,v)->{
					System.out.println("K : "+k.getId());
					System.out.println("Model: "+model.getId());
					if(k.getId().equals(model.getId())) {
						txtUser.clear();
						txtUser.setValue(mapUserOfOrg.get(k).getUserId() == null ? "" : mapUserOfOrg.get(k).getFullName());
						hLayoutUserChoose.setVisible(true);
					}
				});

				return hLayoutUserChoose;
			}).setResizable(true);
		}

		grid.getStyle().setPadding("0");

		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		return grid;
	}

	private void openFormUser(OrganizationModel organizationModel,List<UserOranizationExModel> listUser) {
		taskChooseUserFollowerOfOrgForm.setVisible(true);

		System.out.println(organizationModel.getUserOrganizationExpands());

		taskChooseUserFollowerOfOrgForm.loadData(organizationModel,mapUserOfOrg);
		taskChooseUserFollowerOfOrgForm.getBtnClose().addClickListener(e->{
			closeFormUser();
		});

		taskChooseUserFollowerOfOrgForm.addChangeListener(e->{

			mapUserOfOrg.putAll(taskChooseUserFollowerOfOrgForm.getMapUserIsChoose());
			if(!mapOrgFollowerWasChoose.isEmpty()) {
				taskChooseUserFollowerOfOrgForm.getMapUserIsChoose().forEach((k,v)->{
					mapOrgFollowerWasChoose.forEach((k1,v2)->{
						if(k1.getId().equals(k.getId())) {
							mapOrgFollowerWasChoose.replace(k1, v);
						}
					});
//					if(mapOrgFollowerWasChoose.containsKey(k)) {
//						mapOrgFollowerWasChoose.replace(k, v);
//					}
				});
			}

			loadData();
		});
	}

	private void clearAll() {
		mapUserOfOrg.clear();
		listOrgIsChoose.clear();
		mapOrgIsChoose.clear();
		mapOrgFollowerWasChoose.clear();
		loadData();
		showOrgSelect();
	}

	private void closeFormUser() {
		taskChooseUserFollowerOfOrgForm.setVisible(false);
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

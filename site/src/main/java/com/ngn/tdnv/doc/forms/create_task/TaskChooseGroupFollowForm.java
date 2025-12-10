package com.ngn.tdnv.doc.forms.create_task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.organization.ApiGroupExpandModel;
import com.ngn.api.organization.ApiOrganizationService;
import com.ngn.api.organization.ApiUserGroupExpandModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.ngn.utils.models.model_of_organization.GroupOranizationExModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class TaskChooseGroupFollowForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private boolean isUiMobile = false;
	private String withOfFieldUserInGrid = "120px";
	private String withOfFieldOrgInGrid = "60%";
	
	private HorizontalLayout hLayoutGeneral = new HorizontalLayout();
	private Div hLayoutSelected = new Div();
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm",FontAwesome.Solid.SEARCH.create());
	private TextField txtSearch = new TextField();
	private Checkbox cbAll = new Checkbox();
	private List<Checkbox> listCheckbox = new ArrayList<Checkbox>();
	private Grid<GroupOranizationExModel> grid = new Grid<GroupOranizationExModel>(GroupOranizationExModel.class,false);
	private List<GroupOranizationExModel> listModel = new ArrayList<GroupOranizationExModel>();
	private TaskChooseUserOfGroupFollowForm taskChooseUserOfGroupFollowForm;
	
	private Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapGroupIsChoose = new HashMap<GroupOranizationExModel, ApiUserGroupExpandModel>();

	private String orgId;
	public TaskChooseGroupFollowForm(String orgId,Map<GroupOranizationExModel, ApiUserGroupExpandModel> mapGroupSelected) {
		this.orgId = orgId;
		if(!mapGroupSelected.isEmpty()) {
			mapGroupIsChoose.putAll(mapGroupSelected);
		}
		checkUiMobile();
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		VerticalLayout vLayoutContent = new VerticalLayout();
		vLayoutContent.add(createToolbar(),createGrid());
		
		hLayoutGeneral.setSizeFull();
		
		taskChooseUserOfGroupFollowForm = new TaskChooseUserOfGroupFollowForm(orgId);
		taskChooseUserOfGroupFollowForm.setWidth("50em");
		taskChooseUserOfGroupFollowForm.setVisible(false);
		
		hLayoutGeneral.setFlexGrow(2, vLayoutContent);
		hLayoutGeneral.setFlexGrow(1, taskChooseUserOfGroupFollowForm);
		hLayoutGeneral.add(vLayoutContent,taskChooseUserOfGroupFollowForm);
		
		
		hLayoutSelected.setVisible(false);
		
		this.add(hLayoutGeneral,hLayoutSelected);
	}

	@Override
	public void configComponent() {
		taskChooseUserOfGroupFollowForm.getBtnClose().addClickListener(e->closeFormUser());
		txtSearch.addValueChangeListener(e->loadData());
		btnSearch.addClickListener(e->loadData());
		
		cbAll.addClickListener(e->{
			if(cbAll.getValue()) {
				listCheckbox.forEach(cb->{
					if(!cb.getValue()) {
						listModel.stream().forEach(model->{
							if(cb.getId().get().equals(model.getGroupId())) {
								mapGroupIsChoose.putIfAbsent(model, null);
							}
						});
						cb.setValue(true);
					}
					
				});
			}else {
				listCheckbox.forEach(cb->{
					cb.setValue(false);
				});
				mapGroupIsChoose.clear();
			}
			showOrgSelect();
		});
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
	
	private void loadData() {
		listModel = new ArrayList<GroupOranizationExModel>();
		
		ApiResultResponse<List<ApiGroupExpandModel>> listApiGroups = ApiOrganizationService.getListGroup(orgId,txtSearch.getValue());
		if(listApiGroups.isSuccess()) {
			listModel = listApiGroups.getResult().stream().map(GroupOranizationExModel::new).collect(Collectors.toList());
		}
		
		grid.setItems(listModel);
		
	}
	
	private Component createToolbar() {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		hLayout.setWidthFull();
		hLayout.add(cbAll,txtSearch,btnSearch);
		txtSearch.setWidthFull();
		
		cbAll.getStyle().setMarginTop("5px").setMarginLeft("10px");
		
		return hLayout;
	}
	
	private Component createGrid() {
		grid = new Grid<GroupOranizationExModel>(GroupOranizationExModel.class,false);
		
		
		grid.addComponentColumn(model->{
			Checkbox cbGroup = new Checkbox();
			cbGroup.setId(model.getGroupId());
			
			cbGroup.addClickListener(e->{
				if(cbGroup.getValue()) {
					mapGroupIsChoose.putIfAbsent(model, null);
				}else {
					mapGroupIsChoose.remove(model);
				}
				showOrgSelect();
			});
			
			if( !mapGroupIsChoose.isEmpty() && mapGroupIsChoose.containsKey(model)) {
				cbGroup.setValue(true);
				if(mapGroupIsChoose.get(model) != null) {
					cbGroup.setReadOnly(true);
				}
				showOrgSelect();
			}
			
			listCheckbox.add(cbGroup);
			
			return cbGroup;
		}).setWidth("50px").setFlexGrow(0);
		
		grid.addComponentColumn(model->{
			VerticalLayout vLayoutItem = new VerticalLayout();
			

			H5 header = new H5(model.getName());

			Span spDers = new Span(model.getDescription());
			spDers.getStyle().setColor("hsl(220 min(calc(35% * .8), 35%) 16% / .98)").set("font-style", "italic");
			
			vLayoutItem.add(header,spDers);
			
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
				});

				hLayoutUserChoose.add(txtUser,btnRemove);
				hLayoutUserChoose.setVisible(false);
				
				if(mapGroupIsChoose.containsKey(model)) {
					if(mapGroupIsChoose.get(model)!=null) {
						txtUser.clear();
						txtUser.setValue(mapGroupIsChoose.get(model).getUserName());
						hLayoutUserChoose.setVisible(true);
					}else {
						txtUser.clear();
						hLayoutUserChoose.setVisible(false);
					}
				}
				
				btnRemove.addClickListener(e->{
					if(mapGroupIsChoose.containsKey(model)) {
						mapGroupIsChoose.replace(model, null);
						loadData();
					}
				});
				
				vLayoutItem.add(hLayoutUserChoose);
			}
			return vLayoutItem;
		}).setWidth(withOfFieldOrgInGrid).setFlexGrow(0);
		
		grid.addComponentColumn(model->{
			ButtonTemplate btnUser = new ButtonTemplate("Người dùng ("+model.getUserIds().size()+")");
			
			btnUser.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnUser.addClickListener(e->{
				loadDataUser(model);
			});
			
			return btnUser;
		}).setWidth(withOfFieldUserInGrid).setFlexGrow(0);
		
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
				});

				hLayoutUserChoose.add(txtUser,btnRemove);
				hLayoutUserChoose.setVisible(false);
				
				if(mapGroupIsChoose.containsKey(model)) {
					if(mapGroupIsChoose.get(model)!=null) {
						txtUser.clear();
						txtUser.setValue(mapGroupIsChoose.get(model).getMoreInfo().getFullName());
						hLayoutUserChoose.setVisible(true);
					}else {
						txtUser.clear();
						hLayoutUserChoose.setVisible(false);
					}
				}
				
				btnRemove.addClickListener(e->{
					if(mapGroupIsChoose.containsKey(model)) {
						mapGroupIsChoose.replace(model, null);
						loadData();
					}
				});

				return hLayoutUserChoose;
			});
		}
		
		grid.getStyle().setPadding("0");

		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
		return grid;
	}
	
	private void loadDataUser(GroupOranizationExModel groupOranizationExModel) {
		taskChooseUserOfGroupFollowForm.setVisible(true);
		taskChooseUserOfGroupFollowForm.loadData(groupOranizationExModel,mapGroupIsChoose);
		taskChooseUserOfGroupFollowForm.addChangeListener(e->{
			taskChooseUserOfGroupFollowForm.getMapUserIsChoose().forEach((k,v)->{
				if(mapGroupIsChoose.containsKey(k)) {
					mapGroupIsChoose.replace(k, v);
				}else {
					mapGroupIsChoose.putIfAbsent(k, v);
				}
				
				
			});
			loadData();
		});
	}
	
	private void clearAll() {
		mapGroupIsChoose.clear();
		loadData();
	}
	
	private boolean isMinizime = false;
	private void showOrgSelect() {
		hLayoutSelected.removeAll();
		//This class in file task.css
		hLayoutSelected.addClassName("layout-ord-selected");
		hLayoutSelected.setVisible(true);

		Span spOrg = new Span(mapGroupIsChoose.size()+" đơn vị đã chọn");
		spOrg.getStyle().setColor("white");

		ButtonTemplate btnSave = new ButtonTemplate("Xác nhận");
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
			showOrgSelect();
		});

		confirmDialogTemplate.open();
	}
	
	private void closeFormUser() {
		taskChooseUserOfGroupFollowForm.setVisible(false);
	}

	public Map<GroupOranizationExModel, ApiUserGroupExpandModel> getMapGroupIsChoose() {
		return mapGroupIsChoose;
	}
}

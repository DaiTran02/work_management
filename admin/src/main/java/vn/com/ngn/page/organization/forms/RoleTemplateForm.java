package vn.com.ngn.page.organization.forms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import vn.com.ngn.api.exchange.ApiResultResponse;
import vn.com.ngn.api.organization.ApiOrganizationService;
import vn.com.ngn.api.organization.ApiRoleOrganizationExpandsModel;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.organization.models.RoleOrganizationExpandsModel;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DialogTemplate;

public class RoleTemplateForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private Grid<RoleOrganizationExpandsModel> grid = new Grid<RoleOrganizationExpandsModel>(RoleOrganizationExpandsModel.class,false);
	private List<RoleOrganizationExpandsModel> listModel = new ArrayList<RoleOrganizationExpandsModel>();
	private List<RoleOrganizationExpandsModel> listSelected = new ArrayList<RoleOrganizationExpandsModel>();

	private List<RoleOrganizationExpandsModel> listOfSelectedRoles = new ArrayList<RoleOrganizationExpandsModel>();

	private Checkbox cbAutoUpdate = new Checkbox("Thêm ủy quyền");
	private ButtonTemplate btnWhat = new ButtonTemplate(FontAwesome.Solid.QUESTION_CIRCLE.create());

	private String parentId;
	private List<RoleOrganizationExpandsModel> listRoleWhenCreateOrg;
	public RoleTemplateForm(String parentId,List<RoleOrganizationExpandsModel> listRoleWhenCreateOrg) {
		this.parentId = parentId;
		this.listRoleWhenCreateOrg = listRoleWhenCreateOrg;
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		HorizontalLayout hLayoutCb = new HorizontalLayout();

		btnWhat.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnWhat.setTooltipText("Đây là gì?");


		cbAutoUpdate.getStyle().set("margin-top", "6px");
		hLayoutCb.setSpacing(false);
		hLayoutCb.add(cbAutoUpdate,btnWhat);
		
		cbAutoUpdate.setValue(true);
		
		this.add(hLayoutCb,createGrid());

	}

	@Override
	public void configComponent() {
		btnWhat.addClickListener(e->{
			openDialogAnswer();
		});
		
		cbAutoUpdate.addClickListener(e->{
			loadData();
		});
	}

	public void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiRoleOrganizationExpandsModel>> listDataRoleTemplate = ApiOrganizationService.getListRoleTemplate(0,0,"");
			listModel = listDataRoleTemplate.getResult().stream().map(RoleOrganizationExpandsModel::new).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		loadDataRoleOfOrg();

		grid.setItems(listModel);
		
		grid.deselectAll();
		
		if(cbAutoUpdate.getValue()) {
			grid.getListDataView().getItems().forEach(model->{
				for(RoleOrganizationExpandsModel roleOrganizationExpandsModel : listOfSelectedRoles) {
					if(roleOrganizationExpandsModel.getRoleTemplateId() != null) {
						if(model.getId().equals(roleOrganizationExpandsModel.getRoleTemplateId())) {
							grid.select(model);
						}
					}
				}
			});
		}
		
		

		if(listRoleWhenCreateOrg != null && !listRoleWhenCreateOrg.isEmpty()) {
			grid.getListDataView().getItems().forEach(model->{
				for(RoleOrganizationExpandsModel roleOrganizationExpandsModel : listRoleWhenCreateOrg) {
					if(model.getId().equals(roleOrganizationExpandsModel.getId())) {
						grid.select(model);
					}
				}
			});
		}
	}

	private void loadDataRoleOfOrg() {
		listOfSelectedRoles = new ArrayList<RoleOrganizationExpandsModel>();
		if(parentId != null) {
			try {
				ApiResultResponse<List<ApiRoleOrganizationExpandsModel>> getListRule = ApiOrganizationService.getListRole(parentId);
				listOfSelectedRoles = getListRule.getResult().stream().map(RoleOrganizationExpandsModel::new).collect(Collectors.toList());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Grid<RoleOrganizationExpandsModel> createGrid(){
		grid = new Grid<RoleOrganizationExpandsModel>(RoleOrganizationExpandsModel.class,false);

		grid.setSelectionMode(Grid.SelectionMode.MULTI);
		grid.addSelectionListener(model->{
			listSelected.clear();
			listSelected.addAll(model.getAllSelectedItems());
		});
		
		grid.addColumn(RoleOrganizationExpandsModel::getName).setHeader("Tên").setWidth("25%").setFlexGrow(0).setResizable(true);
		grid.addColumn(RoleOrganizationExpandsModel::getDescription).setHeader("Mô tả");
		

		grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		grid.setAllRowsVisible(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

		return grid;
	}

	public void createRole() {
		
		if(cbAutoUpdate.getValue()) {
			listOfSelectedRoles.forEach(model->{
				listSelected.removeIf(role->role.getId().equals(model.getRoleTemplateId()));
			});
		}
		
		if(!listSelected.isEmpty()) {
			for(RoleOrganizationExpandsModel roleOrganizationExpandsModel : listSelected) {
				doCreateRole(roleOrganizationExpandsModel);
			}
		}
	}

	private void doCreateRole(RoleOrganizationExpandsModel roleOrganizationExpandsModel) {
		try {
			ApiRoleOrganizationExpandsModel apiRoleOrganizationExpandsModel = new ApiRoleOrganizationExpandsModel(roleOrganizationExpandsModel);
			if(cbAutoUpdate.getValue()) {
				apiRoleOrganizationExpandsModel.setRoleTemplateId(roleOrganizationExpandsModel.getId());
			}
			ApiResultResponse<Object> createRole = ApiOrganizationService.createRole(parentId, apiRoleOrganizationExpandsModel);
			if(createRole.getStatus() == 200) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<RoleOrganizationExpandsModel> getListRoleIsChoose(){
		return this.listSelected;
	}

	private void openDialogAnswer() {
		DialogTemplate dialogTemplate = new DialogTemplate("THÊM ỦY QUYỀN LÀ GÌ",()->{

		});

		// This class in file orgnav.css
		Html html = new Html("<div id='table_question'>"
				+ "<table>"
				+ "<tr >"
				+ "<th>Thêm ủy quyền</th>"
				+ "<th>Thêm độc lập</th>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>Đơn vị tự quản và có thể chỉnh sửa quyền hạn</td>"
				+ "<td>Đơn vị tự quản, nhưng không được sửa quyền hạn. Quyền hạn sẽ được cập nhật theo ở vai trò mẫu</td>"
				+ "</tr>"
				+ "</table>"
				+ "</div>");

		dialogTemplate.add(html);

		dialogTemplate.open();
		dialogTemplate.getFooter().removeAll();

		dialogTemplate.setWidth("40%");
		dialogTemplate.setHeight("auto");

	}

}

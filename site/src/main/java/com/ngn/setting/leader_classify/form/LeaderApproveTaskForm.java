package com.ngn.setting.leader_classify.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskModel;
import com.ngn.api.leader_approve_task.ApiLeaderApproveTaskService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.setting.leader_classify.model.FilterClassifyLeaderModel;
import com.ngn.setting.leader_classify.model.LeaderApproveTaskModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.ConfirmDialogTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.NotificationTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class LeaderApproveTaskForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private Grid<LeaderApproveTaskModel> grid = new Grid<LeaderApproveTaskModel>(LeaderApproveTaskModel.class,false);
	private List<LeaderApproveTaskModel> listModel = new ArrayList<LeaderApproveTaskModel>();
	
	private FilterLeaderAndClassifyForm filterLeaderAndClassifyForm = new FilterLeaderAndClassifyForm();
	private int order = 0;
	private BelongOrganizationModel belongOrganizationModel;
	private UserAuthenticationModel userAuthenticationModel;
	public LeaderApproveTaskForm(BelongOrganizationModel belongOrganizationModel,UserAuthenticationModel userAuthenticationModel) {
		this.belongOrganizationModel = belongOrganizationModel;
		this.userAuthenticationModel = userAuthenticationModel;
		buildLayout();
		configComponent();
		loadData();
	}
	
	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(filterLeaderAndClassifyForm,createLayout());
		
	}

	@Override
	public void configComponent() {
		filterLeaderAndClassifyForm.addChangeListener(e->{
			loadData();
		});
		
		filterLeaderAndClassifyForm.getButtonAdd().addClickListener(e->{
			openDiaLogCreateLeader();
		});
	}
	
	private void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiLeaderApproveTaskModel>> data = ApiLeaderApproveTaskService.getListLeader(getParam());
			listModel = data.getResult().stream().map(LeaderApproveTaskModel::new).collect(Collectors.toList());
			filterLeaderAndClassifyForm.setItemPagi(data.getTotal());
			order = data.getTotal();
			fireEvent(new ClickEvent(this,false));
		} catch (Exception e) {
			e.printStackTrace();
		}
		grid.setItems(listModel);
	}
	
	private Component createLayout() {
		grid = new Grid<LeaderApproveTaskModel>(LeaderApproveTaskModel.class,false);
		
		grid.addColumn(LeaderApproveTaskModel::getName).setHeader("Tên");
		grid.addColumn(LeaderApproveTaskModel::getOrganizationName).setHeader("Đơn vị");
		grid.addComponentColumn(model->{
			return new Span(LocalDateUtil.dfDate.format(model.getCreatedTime()));
		}).setHeader("Ngày tạo");
		
//		grid.addComponentColumn(model->{
//			String date = model.getUpdatedTime() == 0 ? "Chưa cập nhật" : LocalDateUtil.dfDate.format(model.getUpdatedTime());
//			return new Span(date);
//		}).setHeader("Ngày cập nhật");
	
		grid.addComponentColumn(model->{
			HorizontalLayout horizontalLayout = new HorizontalLayout();
			ButtonTemplate btnEdit = new ButtonTemplate(FontAwesome.Solid.EDIT.create());
			btnEdit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnEdit.addClickListener(e->{
				openDialogUpdateLeader(model.getId());
			});
			
			ButtonTemplate btnDelete = new ButtonTemplate(FontAwesome.Solid.TRASH.create());
			btnDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY,ButtonVariant.LUMO_ERROR);
			btnDelete.addClickListener(e->{
				openConfirmDialogDelete(model.getId());
			});
			
			horizontalLayout.add(btnEdit,btnDelete);
			
			return horizontalLayout;
			
		}).setHeader("Thao tác").setWidth("100px").setFlexGrow(0);
		grid.setColumnReorderingAllowed(true);
		grid.addThemeVariants(GridVariant.LUMO_COMPACT);
		grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
		grid.addThemeVariants(GridVariant.MATERIAL_COLUMN_DIVIDERS);
		grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
		return grid;
	}
	
	private FilterClassifyLeaderModel getParam() {
		FilterClassifyLeaderModel filterClassifyLeaderModel = filterLeaderAndClassifyForm.getParam();
		filterClassifyLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		return filterClassifyLeaderModel;
	}
	
	private void openDiaLogCreateLeader() {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm mới người duyệt");
		EditLeaderForm editLeaderForm = new EditLeaderForm(null, order, belongOrganizationModel, userAuthenticationModel);
		dialogTemplate.add(editLeaderForm);
		
		dialogTemplate.getBtnSave().addClickListener(e->{
			editLeaderForm.saveLeader();
		});
		
		editLeaderForm.addChangeListener(e->{
			dialogTemplate.close();
			loadData();
		});
		
		dialogTemplate.setWidth("30%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}
	
	private void openDialogUpdateLeader(String idLeader) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chỉnh sửa người duyệt");
		EditLeaderForm editLeaderForm = new EditLeaderForm(idLeader, order, belongOrganizationModel, userAuthenticationModel);
		dialogTemplate.add(editLeaderForm);
		
		dialogTemplate.getBtnSave().addClickListener(e->{
			editLeaderForm.saveLeader();
		});
		
		editLeaderForm.addChangeListener(e->{
			dialogTemplate.close();
			loadData();
		});
		
		dialogTemplate.setWidth("30%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}
	
	private void openConfirmDialogDelete(String idLeader) {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("XÓA NGƯỜI DUYỆT NÀY");
		
		confirmDialogTemplate.setText("Xác nhận xóa");
		confirmDialogTemplate.setDelete();
		confirmDialogTemplate.addConfirmListener(e->{
			doDeleteLeader(idLeader);
		});
		confirmDialogTemplate.open();
	}
	
	private void doDeleteLeader(String idLeader) {
		ApiResultResponse<Object> delete = ApiLeaderApproveTaskService.deleteLeader(idLeader);
		if(delete.isSuccess()) {
			NotificationTemplate.success(delete.getMessage());
			loadData();
		}else {
			NotificationTemplate.error(delete.getMessage());
		}
	}
	

}

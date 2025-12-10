package com.ngn.setting.leader_classify.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.classify_task.ApiClassifyTaskModel;
import com.ngn.api.classify_task.ApiClassifyTaskService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.setting.leader_classify.model.ClassifyTaskModel;
import com.ngn.setting.leader_classify.model.FilterClassifyLeaderModel;
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

public class ClassifyTaskForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private Logger logger = LogManager.getLogger(this);

	private Grid<ClassifyTaskModel> grid = new Grid<ClassifyTaskModel>(ClassifyTaskModel.class,false);
	private List<ClassifyTaskModel> listModel = new ArrayList<ClassifyTaskModel>();
	
	private int order = 0;
	private FilterLeaderAndClassifyForm filterLeaderAndClassifyForm = new FilterLeaderAndClassifyForm();
	
	private BelongOrganizationModel belongOrganizationModel;
	private UserAuthenticationModel userAuthenticationModel;
	public ClassifyTaskForm(BelongOrganizationModel belongOrganizationModel,UserAuthenticationModel userAuthenticationModel) {
		this.userAuthenticationModel = userAuthenticationModel;
		this.belongOrganizationModel = belongOrganizationModel;
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
			openDialogCreateClassify();
		});
		
	}
	
	
	public void loadData() {
		listModel.clear();
		try {
			ApiResultResponse<List<ApiClassifyTaskModel>> data = ApiClassifyTaskService.getListClassify(getParamSearch());
			if(data.isSuccess()) {
				listModel = data.getResult().stream().map(ClassifyTaskModel::new).collect(Collectors.toList());
				filterLeaderAndClassifyForm.setItemPagi(data.getTotal());
				order = data.getTotal();
				fireEvent(new ClickEvent(this,false));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		grid.setItems(listModel);
	}
	
	private Component createLayout() {
		grid = new Grid<ClassifyTaskModel>(ClassifyTaskModel.class,false);
		
		grid.addColumn(ClassifyTaskModel::getName).setHeader("Tên");
		grid.addColumn(ClassifyTaskModel::getOrganizationName).setHeader("Đơn vị");
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
				openDiaLogUpdateClassify(model.getId());
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
	
	private FilterClassifyLeaderModel getParamSearch() {
		FilterClassifyLeaderModel filterClassifyLeaderModel = filterLeaderAndClassifyForm.getParam();
		filterClassifyLeaderModel.setOrganizationId(belongOrganizationModel.getOrganizationId());
		return filterClassifyLeaderModel;
	}
	
	private void openDialogCreateClassify() {
		DialogTemplate dialogTemplate = new DialogTemplate("Thêm mới phân loại chỉ đạo");
		EditClassifyForm editClassifyForm = new EditClassifyForm(null,belongOrganizationModel,userAuthenticationModel,order);
		dialogTemplate.add(editClassifyForm);
		
		dialogTemplate.getBtnSave().addClickListener(e->{
			editClassifyForm.saveClassify();
		});
		
		editClassifyForm.addChangeListener(e->{
			dialogTemplate.close();
			loadData();
		});
		
		dialogTemplate.setWidth("30%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}

	private void openDiaLogUpdateClassify(String idClassify) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chỉnh sửa phân loại chỉ đạo");
		EditClassifyForm editClassifyForm = new EditClassifyForm(idClassify,belongOrganizationModel,userAuthenticationModel,order);
		dialogTemplate.add(editClassifyForm);
		
		dialogTemplate.getBtnSave().addClickListener(e->{
			editClassifyForm.saveClassify();
		});
		
		editClassifyForm.addChangeListener(e->{
			dialogTemplate.close();
			loadData();
		});
		
		dialogTemplate.setWidth("30%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.open();
	}
	
	private void openConfirmDialogDelete(String idClassify) {
		ConfirmDialogTemplate confirmDialogTemplate = new ConfirmDialogTemplate("XÓA PHÂN LOẠI CHỈ ĐẠO");
		
		confirmDialogTemplate.setText("Xác nhận xóa");
		confirmDialogTemplate.setDelete();
		confirmDialogTemplate.addConfirmListener(e->{
			doDeleteClassify(idClassify);
		});
		confirmDialogTemplate.open();
	}
	
	private void doDeleteClassify(String idClassify) {
		ApiResultResponse<Object> delete = ApiClassifyTaskService.deleteClassify(idClassify);
		if(delete.isSuccess()) {
			NotificationTemplate.success(delete.getMessage());
			loadData();
		}else {
			NotificationTemplate.error(delete.getMessage());
		}
	}
	
}







































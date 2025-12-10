package com.ngn.tdnv.personal_record.form;

import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.personal_record.ApiPersonalFilter;
import com.ngn.api.personal_record.ApiPersonalRecordModel;
import com.ngn.api.personal_record.ApiPersonalService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class ListPersonalTransferredForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	private UserAuthenticationModel user = SessionUtil.getUser();
	private Grid<ApiPersonalRecordModel> grid = new Grid<ApiPersonalRecordModel>(ApiPersonalRecordModel.class,false);
	private List<ApiPersonalRecordModel> listModel = new ArrayList<ApiPersonalRecordModel>();
	private ButtonTemplate btnSearch = new ButtonTemplate(FontAwesome.Solid.SEARCH.create());
	private TextField txtSearch = new TextField();
	
	
	public ListPersonalTransferredForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		this.add(createFilter(),createGrid());
	}

	@Override
	public void configComponent() {
		txtSearch.addValueChangeListener(e->loadData());
		btnSearch.addClickListener(e->loadData());
	}
	
	private void loadData() {
		ApiResultResponse<List<ApiPersonalRecordModel>> data = ApiPersonalService.getListPersonalTransferred(getSearch());
		listModel.clear();
		if(data.isSuccess()) {
			listModel.addAll(data.getResult());
		}
		
		grid.setItems(listModel);
	}
	
	private Component createGrid() {
		grid = new Grid<ApiPersonalRecordModel>(ApiPersonalRecordModel.class,false);
		
		grid.addColumn(ApiPersonalRecordModel::getTitle).setHeader("Tên hồ sơ");
		grid.addColumn(ApiPersonalRecordModel::getDescription).setHeader("Mô tả");
		grid.addColumn(model->{
			return LocalDateUtil.dfDate.format(model.getTransferTime());
		}).setHeader("Ngày chuyển giao");
		
		grid.addComponentColumn(model->{
			HorizontalLayout hLayout = new HorizontalLayout();
			
			ButtonTemplate btnView = new ButtonTemplate("Xem chi tiết",FontAwesome.Solid.EYE.create());
			btnView.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnView.addClickListener(e->openDialogViewDetailTransferred(model.getId()));
			
			
			hLayout.add(btnView);
			
			
			return hLayout;
		}).setHeader("Thao tác");
		
		
		return grid;
	}
	
	private Component createFilter() {
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.add(txtSearch, btnSearch);
		
		
		txtSearch.setPlaceholder("Tìm theo tên");
		
		hLayout.expand(txtSearch);
		hLayout.setWidthFull();
		
		return hLayout;
	}
	
	private ApiPersonalFilter getSearch() {
		ApiPersonalFilter filter = new ApiPersonalFilter();
		filter.setTransferredUserId(user.getId());
		filter.setKeySearch("");
		
		return filter;
	}
	
	private void openDialogViewDetailTransferred(String id) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chi tiết hồ sơ");
		TransferredForm transferredForm = new TransferredForm(id);
		dialogTemplate.add(transferredForm);
		
		dialogTemplate.open();
		dialogTemplate.setWidth("70%");
		dialogTemplate.setLayoutMobile();
		dialogTemplate.getFooter().removeAll();
	}

}

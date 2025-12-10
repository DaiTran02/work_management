package com.ngn.tdnv.personal_record.form;

import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.personal_record.ApiPersonalDetailModel;
import com.ngn.api.personal_record.ApiPersonalFilter;
import com.ngn.api.personal_record.ApiPersonalRecordModel;
import com.ngn.api.personal_record.ApiPersonalService;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.UserAuthenticationModel;
import com.ngn.tdnv.personal_record.model.DataChooseModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.DialogTemplate;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class ListPersonalRecordTransferForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	//Detail
	private VerticalLayout vLayoutDetail = new VerticalLayout();
	private TextField txtTitle = new TextField("Tên hồ sơ");
	private TextArea txtDescrip = new TextArea("Mô tả");
	private Grid<DataChooseModel> gridDetail = new Grid<DataChooseModel>(DataChooseModel.class,false);
	private List<DataChooseModel> listDetail = new ArrayList<DataChooseModel>();
	
	
	private TextField txtSearch = new TextField("Tìm kiếm");
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm",FontAwesome.Solid.SEARCH.create());
	private Grid<ApiPersonalRecordModel> grid = new Grid<ApiPersonalRecordModel>(ApiPersonalRecordModel.class,false);
	private List<ApiPersonalRecordModel> listModel = new ArrayList<ApiPersonalRecordModel>();
	private UserAuthenticationModel userAuthenticationModel = SessionUtil.getUser();
	
	public ListPersonalRecordTransferForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		HorizontalLayout hLayoutFilter = new HorizontalLayout();
		hLayoutFilter.setWidthFull();
		hLayoutFilter.add(txtSearch,btnSearch);
		hLayoutFilter.expand(txtSearch);
		txtSearch.setWidthFull();
		txtSearch.setPlaceholder("Tìm kiếm...");
		btnSearch.getStyle().setMarginTop("30px");
		
		this.add(hLayoutFilter,createGrid());
	}

	@Override
	public void configComponent() {
		btnSearch.addClickListener(e->loadData());
		txtSearch.addValueChangeListener(e->loadData());
	}
	
	private void loadData() {
		listModel = new ArrayList<ApiPersonalRecordModel>();
		ApiResultResponse<List<ApiPersonalRecordModel>> data = ApiPersonalService.getListOldPersonal(getSearch());
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
			return model.getCurrentUser().getFullName();
		}).setHeader("Người nhận");
		grid.addColumn(model->{
			return LocalDateUtil.dfDateTime.format(model.getTransferTime());
		}).setHeader("Ngày chuyển giao");
		
		grid.addComponentColumn(model->{
			ButtonTemplate btnView = new ButtonTemplate("Chi tiết",FontAwesome.Solid.EYE.create());
			btnView.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			btnView.addClickListener(e->openDialogDetail(model.getId()));
			
			return btnView;
		}).setHeader("Thao tác").setWidth("110px").setFlexGrow(0);
		
		return grid;
	}
	
	private ApiPersonalFilter getSearch() {
		ApiPersonalFilter apiPersonalFilter = new ApiPersonalFilter();
		apiPersonalFilter.setOldUserId(userAuthenticationModel.getId());
		apiPersonalFilter.setKeySearch(txtSearch.getValue());
		return apiPersonalFilter;
	}
	
	private void openDialogDetail(String id) {
		DialogTemplate dialogTemplate = new DialogTemplate("Chi tiết hồ sơ");
		dialogTemplate.add(vLayoutDetail);
		loadLayoutDetail();
		loadDataDetail(id);
		
		dialogTemplate.getFooter().removeAll();
		
		dialogTemplate.setHeight("90%");
		dialogTemplate.setWidth("70%");
		
		dialogTemplate.open();
	}
	
	private void loadDataDetail(String id) {
		listDetail = new ArrayList<DataChooseModel>();
		ApiResultResponse<ApiPersonalDetailModel> data = ApiPersonalService.getDetailPersonal(id);
		if(data.isSuccess()) {
			ApiPersonalDetailModel apiPersonalDetailModel = data.getResult();
			txtTitle.setValue(apiPersonalDetailModel.getTitle());
			txtDescrip.setValue(apiPersonalDetailModel.getDescription());
			apiPersonalDetailModel.getDocs().forEach(model->{
				DataChooseModel dataChooseModel = new DataChooseModel(model);
				listDetail.add(dataChooseModel);
			});
			
			apiPersonalDetailModel.getTasks().forEach(model->{
				DataChooseModel dataChooseModel = new DataChooseModel(model);
				listDetail.add(dataChooseModel);
			});
		}
		gridDetail.setItems(listDetail);
	}
	
	private void loadLayoutDetail() {
		vLayoutDetail.removeAll();
		
		txtTitle.setWidthFull();
		txtTitle.setReadOnly(true);
		
		txtDescrip.setWidthFull();
		txtDescrip.setHeight("200px");
		txtDescrip.setReadOnly(true);
		
		gridDetail.addColumn(DataChooseModel::getName).setHeader("Tiêu đề").setResizable(true);
		gridDetail.addColumn(DataChooseModel::getType).setHeader("Loại thông tin");
		gridDetail.addColumn(DataChooseModel::getCreateTime).setHeader("Ngày tạo");
		
		vLayoutDetail.add(txtTitle,txtDescrip,gridDetail);
	}

}

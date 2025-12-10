package com.ngn.tdnv.personal_record.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.api.tasks.ApiFilterTaskModel;
import com.ngn.api.tasks.ApiOutputTaskModel;
import com.ngn.api.tasks.ApiTaskService;
import com.ngn.interfaces.FormInterface;
import com.ngn.models.BelongOrganizationModel;
import com.ngn.utils.LocalDateUtil;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.PaginationForm;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

public class ChooseTaskForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private DateTimePicker dateStartDay = new DateTimePicker("Từ ngày");
	private DateTimePicker dateEndDay = new DateTimePicker("Đến ngày");
	private TextField txtSeach = new TextField("Tìm kiếm");
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm kiếm",FontAwesome.Solid.SEARCH.create());
	
	
	private BelongOrganizationModel belongOrganizationModel = SessionUtil.getOrg();
	
	private PaginationForm paginationForm;
	private Grid<ApiOutputTaskModel> grid = new Grid<ApiOutputTaskModel>(ApiOutputTaskModel.class,false);
	private List<ApiOutputTaskModel> listModel = new ArrayList<ApiOutputTaskModel>();
	private List<ApiOutputTaskModel> listTaskChoose = new ArrayList<ApiOutputTaskModel>();
	
	public ChooseTaskForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		
		this.add(createFilter());
		
		
		paginationForm = new PaginationForm(()->{
			if(paginationForm != null) loadData();
		});
		
		paginationForm.getCmbItem().setValue(20);
		
		this.add(paginationForm,createGrid());
		
	}

	@Override
	public void configComponent() {
		btnSearch.addClickListener(e->loadData());
	}
	
	private void loadData() {
		listModel = new ArrayList<ApiOutputTaskModel>();
		try {
			int countTask = 0;
			ApiResultResponse<List<ApiOutputTaskModel>> dataOwner = ApiTaskService.getListTaskOwner(getSearch(Pair.of("own",belongOrganizationModel.getOrganizationId())));
			countTask += dataOwner.getTotal();
			dataOwner.getResult().stream().forEach(model->{
				model.setState("Nhiệm vụ đã giao");
				listModel.add(model);
			});
			
			ApiResultResponse<List<ApiOutputTaskModel>> dataAssinee = ApiTaskService.getListAssignee(getSearch(Pair.of("assignee",belongOrganizationModel.getOrganizationId())));
			countTask += dataAssinee.getTotal();
			dataAssinee.getResult().stream().forEach(model->{
				model.setState("Nhiệm vụ được giao");
				listModel.add(model);
			});
			
			paginationForm.setItemCount(countTask);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		grid.setItems(listModel);
		
	}
	
	private Component createGrid() {
		grid = new Grid<ApiOutputTaskModel>(ApiOutputTaskModel.class,false);
		
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.addSelectionListener(e->{
			listTaskChoose.clear();
			listTaskChoose.addAll(e.getAllSelectedItems());
		});
		grid.addColumn(ApiOutputTaskModel::getTitle).setHeader("Tiêu đề");
		grid.addColumn(ApiOutputTaskModel::getState).setHeader("Loại nhiệm vụ").setWidth("200px").setFlexGrow(0);
		
		return grid;
	}
	
	private Component createFilter() {
		HorizontalLayout hLayout = new HorizontalLayout();
		
		dateStartDay.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		dateStartDay.setWidth("180px");

		dateEndDay.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
		dateEndDay.setWidth("180px");
		
		dateStartDay.setLocale(LocalDateUtil.localeVietNam());
		dateStartDay.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getStartOfTheYear(SessionUtil.getYear()))));


		dateEndDay.setValue(LocalDateUtil.longToLocalDateTime(LocalDateUtil.localDateTimeToLong(LocalDateUtil.getEndtOfTheYear(SessionUtil.getYear()))));
		dateEndDay.setLocale(LocalDateUtil.localeVietNam());

		txtSeach.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtSeach.setWidth("250px");
		txtSeach.setClearButtonVisible(true);
		txtSeach.setPlaceholder("Nhập từ khóa để tìm...");
		
		btnSearch.addThemeVariants(ButtonVariant.LUMO_SMALL);
		btnSearch.getStyle().setMarginTop("28px");
		
		hLayout.setWidthFull();
		hLayout.add(dateStartDay,dateEndDay,txtSeach,btnSearch);
		hLayout.expand(txtSeach);
		
		return hLayout;
	}
	
	private ApiFilterTaskModel getSearch(Pair<String, String> item) {
		ApiFilterTaskModel apiFilterTaskModel = new ApiFilterTaskModel();
		apiFilterTaskModel.setSkip(paginationForm.getSkip());
		apiFilterTaskModel.setLimit(paginationForm.getLimit());
		apiFilterTaskModel.setToDate(LocalDateUtil.localDateTimeToLong(dateEndDay.getValue()));
		apiFilterTaskModel.setFromDate(LocalDateUtil.localDateTimeToLong(dateStartDay.getValue()));
		apiFilterTaskModel.setKeyword(txtSeach.getValue());
		
		switch(item.getKey()) {
		case "own":
			apiFilterTaskModel.setOwnerOrganizationId(item.getValue());
			break;
		case "assignee":
			apiFilterTaskModel.setAssigneeOrganizationId(item.getValue());
			break;
		}
		
		return apiFilterTaskModel;
	}

	public List<ApiOutputTaskModel> getListTaskChoose() {
		return listTaskChoose;
	}
}

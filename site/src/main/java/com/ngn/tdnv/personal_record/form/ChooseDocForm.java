package com.ngn.tdnv.personal_record.form;

import java.util.ArrayList;
import java.util.List;

import com.ngn.api.doc.ApiDocModel;
import com.ngn.api.doc.ApiDocService;
import com.ngn.api.doc.ApiFilterListDocModel;
import com.ngn.api.result.ApiResultResponse;
import com.ngn.interfaces.FormInterface;
import com.ngn.tdnv.doc.forms.DocFilterForm;
import com.ngn.utils.SessionUtil;
import com.ngn.utils.components.PaginationForm;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

public class ChooseDocForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;

	private DocFilterForm docFilterForm = new DocFilterForm(SessionUtil.getOrg(), null, null);
	private PaginationForm paginationForm;
	private Grid<ApiDocModel> grid = new Grid<ApiDocModel>(ApiDocModel.class,false);
	private List<ApiDocModel> listModel = new ArrayList<ApiDocModel>();
	
	private List<ApiDocModel> listDocChoose = new ArrayList<ApiDocModel>();
	
	public ChooseDocForm() {
		buildLayout();
		configComponent();
		loadData();
	}

	@Override
	public void buildLayout() {
		this.setSizeFull();
		paginationForm = new PaginationForm(()->loadData());
		this.add(docFilterForm,paginationForm,createGrid());
	}

	@Override
	public void configComponent() {
		docFilterForm.addChangeListener(e->loadData());
	}
	
	private void loadData() {
		listModel = new ArrayList<ApiDocModel>();
		try {
			ApiResultResponse<List<ApiDocModel>> data = ApiDocService.getListDoc(getSearch());
			if(data.isSuccess()) {
				listModel.addAll(data.getResult());
				paginationForm.setItemCount(data.getTotal());
			}
		} catch (Exception e) {
		}
		grid.setItems(listModel);
	}
	
	
	private Component createGrid() {
		grid = new Grid<ApiDocModel>(ApiDocModel.class,false);
		
		grid.setSelectionMode(SelectionMode.MULTI);
		grid.addColumn(ApiDocModel::getSummary).setHeader("Tiêu đề").setResizable(true);
		grid.addColumn(ApiDocModel::getNumber).setHeader("Số hiệu");
		grid.addColumn(ApiDocModel::getSymbol).setHeader("Ký hiệu");
		grid.addSelectionListener(e->{
			listDocChoose.clear();
			listDocChoose.addAll(e.getAllSelectedItems());
		});
		
		return grid;
	}
	
	private ApiFilterListDocModel getSearch() {
		ApiFilterListDocModel apiFilterListDocModel = docFilterForm.getFilter();
		apiFilterListDocModel.setLimit(paginationForm.getLimit());
		apiFilterListDocModel.setSkip(paginationForm.getSkip());
		return apiFilterListDocModel;
	}

	public List<ApiDocModel> getListDocChoose() {
		return listDocChoose;
	}
}

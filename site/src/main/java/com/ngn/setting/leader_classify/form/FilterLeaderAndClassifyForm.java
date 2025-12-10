package com.ngn.setting.leader_classify.form;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.interfaces.FormInterface;
import com.ngn.setting.leader_classify.model.FilterClassifyLeaderModel;
import com.ngn.utils.components.ButtonTemplate;
import com.ngn.utils.components.PaginationForm;
import com.ngn.utils.components.VerticalLayoutTemplate;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.dom.Style.FlexWrap;

public class FilterLeaderAndClassifyForm extends VerticalLayoutTemplate implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout verticalLayout = new VerticalLayout();
	
	private HorizontalLayout hLayout = new HorizontalLayout();
	
	private TextField txtSearch = new TextField("Nhập từ khóa để tìm");
	private ButtonTemplate btnSearch = new ButtonTemplate("Tìm kiếm",FontAwesome.Solid.SEARCH.create());
	private Checkbox cbActive = new Checkbox("Hoạt động");
	private ButtonTemplate btnAdd = new ButtonTemplate("Thêm",FontAwesome.Solid.PLUS.create());
	private PaginationForm paginationForm;

	public FilterLeaderAndClassifyForm() {
		buildLayout();
		configComponent();
		checkLayoutMobile();
	}
	
	@Override
	public void buildLayout() {
		paginationForm = new PaginationForm(()->{
			fireEvent(new ClickEvent(this,false));
		});
		this.add(verticalLayout);
		createLayout();
	}

	@Override
	public void configComponent() {
		btnSearch.addClickListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
		cbActive.addValueChangeListener(e->{
			fireEvent(new ClickEvent(this,false));
		});
		
	}
	
	private void checkLayoutMobile() {
		try {
			UI.getCurrent().getPage().retrieveExtendedClientDetails(e->{
				if(e.getScreenWidth() < 768) {
					paginationForm.setLayoutMobile();
					hLayout.getStyle().setDisplay(Display.FLEX).setFlexWrap(FlexWrap.WRAP);
				}
			});
		} catch (Exception e) {
		
		}
	}
	
	private void createLayout() {
		verticalLayout.removeAll();
		hLayout.removeAll();
		
		txtSearch.setWidthFull();
		txtSearch.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		txtSearch.setClearButtonVisible(true);
		
		cbActive.setWidth("150px");
		cbActive.getStyle().setMarginTop("29px");
		cbActive.setValue(true);
		
		btnSearch.getStyle().setMarginTop("27px");
		btnSearch.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		btnAdd.getStyle().setMarginTop("27px");
		btnAdd.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		
		hLayout.setSizeFull();
		hLayout.expand(txtSearch);
		hLayout.add(txtSearch,cbActive,btnSearch,btnAdd);
		
		
		verticalLayout.setWidthFull();
		verticalLayout.add(hLayout,paginationForm);
	}
	
	public FilterClassifyLeaderModel getParam() {
		FilterClassifyLeaderModel filterClassifyLeaderModel = new FilterClassifyLeaderModel();
		
		filterClassifyLeaderModel.setSkip(paginationForm.getSkip());
		filterClassifyLeaderModel.setLimit(paginationForm.getLimit());
		filterClassifyLeaderModel.setActive(cbActive.getValue());
		filterClassifyLeaderModel.setKeyword(txtSearch.getValue());
		
		
		return filterClassifyLeaderModel;
	}
	
	public void setItemPagi(int count) {
		paginationForm.setItemCount(count);
	}
	
	public ButtonTemplate getButtonAdd() {
		return btnAdd;
	}

}

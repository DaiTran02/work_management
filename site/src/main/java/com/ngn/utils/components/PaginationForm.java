package com.ngn.utils.components;

import java.util.ArrayList;
import java.util.List;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.ngn.interfaces.FormInterface;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;


public class PaginationForm extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;


	private HorizontalLayout hLayoutMain = new HorizontalLayout();

	private HorizontalLayout hLeftLayout = new HorizontalLayout();
	private Span textItem1 = new Span("Hiện thị");
	private ComboBox<Integer> cmbItem = new ComboBox<Integer>();
	private Span textItem2 = new Span("Dòng");
	private HorizontalLayout hTotal = new HorizontalLayout();
	private Span textTotal1 = new Span("Tổng số");
	private Span textTotalDefault = new Span("0");

	private HorizontalLayout hRightLayout = new HorizontalLayout();
	private Button btnFirst = new Button(FontAwesome.Solid.ANGLE_DOUBLE_LEFT.create());
	private Button btnPrevious = new Button(FontAwesome.Solid.ANGLE_LEFT.create());
	private Button btnNext = new Button(FontAwesome.Solid.ANGLE_RIGHT.create());
	private Button btnLast = new Button(FontAwesome.Solid.ANGLE_DOUBLE_RIGHT.create());
	private IntegerField txtPageNumber = new IntegerField();
	private Span spanSlash = new Span("/");
	private Span spanPage = new Span();

	private Button btnTrigger = new Button();

	private int itemPerPage = 0;
	private int currentPage = 1;
	private int maxPage = 0;
	private int skip = 0;
	private int limit = 0;

	private int itemCount = 0;

	private Runnable run;
	public PaginationForm(Runnable run) {
		this.run = run;
		buildLayout();
		configComponent();
		initValue();
	}

	@Override
	public void buildLayout() {
		this.add(hLayoutMain,btnTrigger);
		btnTrigger.setVisible(false);
		hLayoutMain.add(hLeftLayout,hRightLayout);
		hLayoutMain.expand(hLeftLayout);
		hLayoutMain.setWidthFull();
		this.setWidthFull();
		this.setPadding(false);

		buildRightLayout();
		buidLayoutLeft();

	}

	@Override
	public void configComponent() {
		cmbItem.addValueChangeListener(e->{
			reCalculate();
			calculateSkipLimit();
			run.run();
		});

		btnFirst.addClickListener(e->{
			int page = 1;
			txtPageNumber.setValue(page);
			currentPage = page;
			calculateSkipLimit();
			run.run();
		});

		btnPrevious.addClickListener(e->{
			int page = currentPage >1 ? currentPage -1 :1;
			txtPageNumber.setValue(page);
			currentPage = page;
			calculateSkipLimit();
			run.run();
		});

		btnNext.addClickListener(e->{
			int page = currentPage < maxPage ? currentPage + 1 : maxPage;
			txtPageNumber.setValue(page);
			currentPage = page;

			calculateSkipLimit();
			run.run();
		});

		btnLast.addClickListener(e->{
			int page = maxPage;
			txtPageNumber.setValue(page);
			currentPage = page;

			calculateSkipLimit();
			run.run();
		});

		txtPageNumber.addKeyDownListener(Key.ENTER, e->{
			int value = txtPageNumber.getValue();
			if(value < 0)
				value = 1;
			else if(value > maxPage)
				value = maxPage;
			txtPageNumber.setValue(value);

			currentPage = value;
			calculateSkipLimit();
			run.run();
		});

	}

	private void initValue() {
		List<Integer> listNumbers = new ArrayList<Integer>();
		listNumbers.add(5);
		listNumbers.add(10);
		listNumbers.add(30);
		listNumbers.add(50);
		listNumbers.add(100);

		cmbItem.setItems(listNumbers);
		cmbItem.setValue(listNumbers.get(0));

		txtPageNumber.setValue(1);

	}

	private void buidLayoutLeft() {
		hLeftLayout.add(textItem1,cmbItem,textItem2);
		textItem1.getStyle().set("font-size", "var(--lumo-font-size-s)");
		textItem2.getStyle().set("font-size", "var(--lumo-font-size-s)");

		cmbItem.setWidth("80px");
		cmbItem.addThemeVariants(ComboBoxVariant.LUMO_SMALL);
		hTotal.add(textTotal1,textTotalDefault);

		textTotal1.getStyle().set("font-size", "var(--lumo-font-size-s)");
		textTotalDefault.getStyle().set("font-size", "var(--lumo-font-size-m)");
		hTotal.addClassName("total-display");
		hTotal.setDefaultVerticalComponentAlignment(Alignment.END);
//		hLeftLayout.getStyle().set("margin-left", "20px");

		hLeftLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
	}

	private void buildRightLayout() {
		btnFirst.getStyle().setCursor("pointer");
		btnFirst.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		btnPrevious.getStyle().setCursor("pointer");
		btnPrevious.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		btnNext.getStyle().setCursor("pointer");
		btnNext.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		btnLast.getStyle().setCursor("pointer");
		btnLast.addThemeVariants(ButtonVariant.LUMO_SMALL);
		
		hRightLayout.add(btnFirst,btnPrevious,txtPageNumber,spanSlash,spanPage,btnNext,btnLast);

		txtPageNumber.setMin(1);
		txtPageNumber.setWidth("80px");
		txtPageNumber.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
		txtPageNumber.addThemeVariants(TextFieldVariant.LUMO_SMALL);
		
		spanSlash.getStyle().set("font-size", "var(--lumo-font-size-s)");
		spanPage.getStyle().set("font-size", "var(--lumo-font-size-s)");
		hRightLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
	}
	
	public void setLayoutMobile() {
		hLeftLayout.removeAll();
		hLeftLayout.add(cmbItem);
		cmbItem.setWidth("55px");
		
		txtPageNumber.setWidth("30px");
		
	}

	public void reCalculate() {
		itemPerPage = cmbItem.getValue();
		maxPage = (itemCount % itemPerPage) == 0 ? itemCount / itemPerPage : (itemCount / itemPerPage) + 1;
		currentPage = currentPage > maxPage ? maxPage : currentPage;

		if(currentPage==0)
			currentPage = 1;
		calculateSkipLimit();
		txtPageNumber.setValue(currentPage);
		txtPageNumber.setMax(maxPage);
		spanPage.setText(String.valueOf(maxPage));
		textTotalDefault.setText(String.valueOf(itemCount));
	}

	private void calculateSkipLimit() {
		skip = currentPage == 1 ? 0 :(currentPage - 1) * itemPerPage;
		limit = itemPerPage;
		btnTrigger.click();

	}

	public Button getBtnTrigger() {
		return btnTrigger;
	}
	public int getSkip() {
		return skip;
	}
	public int getLimit() {
		return limit;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
		this.reCalculate();
		this.calculateSkipLimit();
		textItem2.setText("dòng/"+itemCount);
	}
	
	public ComboBox<Integer> getCmbItem(){
		return this.cmbItem;
	}

}

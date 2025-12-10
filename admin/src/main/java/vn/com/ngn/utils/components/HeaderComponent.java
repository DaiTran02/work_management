package vn.com.ngn.utils.components;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import vn.com.ngn.interfaces.FormInterface;

public class HeaderComponent extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;
	
	private String title;
	private Button btnAdd = new Button("Thêm người dùng mới",FontAwesome.Solid.PLUS.create());
	private ButtonTemplate btnAddUserFromOrther = new ButtonTemplate("Thêm người dùng sẵn có", FontAwesome.Solid.FILE_IMPORT.create());
	private ButtonTemplate btnSaveNewChange = new ButtonTemplate("Lưu trạng thái",FontAwesome.Solid.SAVE.create());
	
	public HeaderComponent(String title) {
		this.title = title;
		buildLayout();
		configComponent();
	}

	@Override
	public void buildLayout() {
		this.setSpacing(false);
		this.setPadding(false);
		
	}

	@Override
	public void configComponent() {
		
	}
	
	public void add(Component component) {
		this.add(createLayout(),component);
	}
	
	private Component createLayout() {
		HorizontalLayout headerLayout = new HorizontalLayout();

		Html htmlDeviceStatus = new Html("<div style='width:auto;  color:white; padding-left:10px;'>"
				+ "<p style='font-size:14px; color: black; font-weight:600;'>"+title+"</p>"
				+ "</div>");

		headerLayout.getStyle().set("background","rgb(205 205 205 / 35%)").set("border-bottom", "1px solid rgb(131 131 131)");
		
		headerLayout.setWidth("100%");
		headerLayout.setHeight("35px");
		
		
		btnAdd.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnAdd.getStyle().setCursor("pointer").set("margin-right", "15px");
		
		btnSaveNewChange.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		btnSaveNewChange.setVisible(false);
		
		HorizontalLayout hLayoutButton = new HorizontalLayout();
		hLayoutButton.add(btnSaveNewChange,btnAdd,btnAddUserFromOrther);
		hLayoutButton.getStyle().set("margin-left", "auto");
		
		headerLayout.add(htmlDeviceStatus,hLayoutButton);
		return headerLayout;
	}

	public Button getBtnAdd() {
		return btnAdd;
	}
	
	
	public ButtonTemplate getBtnAddUserFromOrther() {
		return btnAddUserFromOrther;
	}

	public Button getBtnSave() {
		return btnSaveNewChange;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}

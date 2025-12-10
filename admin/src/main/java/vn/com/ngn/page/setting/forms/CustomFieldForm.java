package vn.com.ngn.page.setting.forms;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import vn.com.ngn.page.report.forms.ChooseOrganizationForm;
import vn.com.ngn.page.setting.forms.details.EditCustomFieldForm;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.utils.components.VerticalLayoutTemplate;

public class CustomFieldForm extends VerticalLayoutTemplate{
	private static final long serialVersionUID = 1L;
	
	private Button btnChooseOrg = new Button("Chọn đơn vị quản lý",FontAwesome.Solid.FOLDER_TREE.create());
	private TextField txtOrg = new TextField("Tên đơn vị quản lý");
	private ButtonTemplate btnCreateComponent = new ButtonTemplate("Thêm thành phần mới vào giao diện");
	private ChooseOrganizationForm chooseOrganizationForm;
	@SuppressWarnings("unused")
	private String orgId = null;
	
	public CustomFieldForm() {
		buildLayout();
		configComponent();
	}
	
	private void buildLayout() {
		this.setSizeFull();
		this.add(createToolbar());
		
	}
	
	private void configComponent() {
		btnChooseOrg.addClickListener(e->{
			openDialogChooseOrg();
		});
		btnCreateComponent.addClickListener(e->openDialogEditField());
	}
	
	
	private Component createToolbar() {
		VerticalLayout layoutGenera = new VerticalLayout();
		
		HorizontalLayout hLayout = new HorizontalLayout();

		btnChooseOrg.getStyle().setCursor("pointer").set("margin-top", "30px");
		btnCreateComponent.getStyle().set("margin-top", "30px");

		txtOrg.setReadOnly(true);
		txtOrg.setWidthFull();
		

		hLayout.setWidthFull();
		hLayout.add(btnChooseOrg,txtOrg,btnCreateComponent);
		hLayout.expand(txtOrg);
		
		layoutGenera.add(hLayout);
		layoutGenera.setWidthFull();

		return layoutGenera;
	}
	
	
	private void openDialogChooseOrg() {
		DialogTemplate dialog = new DialogTemplate("CHỌN ĐƠN VỊ",()->{

		});

		chooseOrganizationForm = new ChooseOrganizationForm(null, ()->{
			txtOrg.setValue(chooseOrganizationForm.getOrg().getName());
			orgId = chooseOrganizationForm.getOrg().getId();
			dialog.close();
		});

		dialog.add(chooseOrganizationForm);
		dialog.setWidth("90%");
		dialog.setHeight("90%");
		dialog.getBtnSave().setVisible(false);
		dialog.getBtnSave().addClickListener(e->{
			dialog.close();
		});
		dialog.open();
	}
	
	private void openDialogEditField() {
		DialogTemplate dialogTemplate = new DialogTemplate("Tạo thành phần",()->{});
		EditCustomFieldForm editCustomFieldForm = new EditCustomFieldForm();
		dialogTemplate.add(editCustomFieldForm);
		dialogTemplate.setSizeFull();
		dialogTemplate.setWidth("60%");
		dialogTemplate.open();
	}
	
	

}

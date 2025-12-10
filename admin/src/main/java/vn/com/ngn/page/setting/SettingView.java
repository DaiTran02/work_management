package vn.com.ngn.page.setting;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
import vn.com.ngn.interfaces.FormInterface;
import vn.com.ngn.page.setting.forms.ControlRoleTemplateForm;
import vn.com.ngn.page.setting.forms.CustomFieldForm;
import vn.com.ngn.page.setting.forms.ListLogRequestForm;
import vn.com.ngn.page.setting.forms.OrganizationCategoryForm;
import vn.com.ngn.utils.components.ButtonTemplate;
import vn.com.ngn.utils.components.DialogTemplate;
import vn.com.ngn.views.MainLayout;

@Route(value = "setting",layout = MainLayout.class)
@PageTitle(value = "Cài đặt")
@PermitAll
public class SettingView extends VerticalLayout implements FormInterface{
	private static final long serialVersionUID = 1L;

	private ButtonTemplate btnRoleTemplate = new ButtonTemplate("Quản lý vai trò mẫu",FontAwesome.Solid.TOOLS.create());
	private ButtonTemplate btnLogRequest = new ButtonTemplate("Lịch sử thực hiện",FontAwesome.Solid.HISTORY.create());
	private ButtonTemplate btnOrgCatogory = new ButtonTemplate("Quản lý loại đơn vị",FontAwesome.Solid.BAHAI.create());
	private ButtonTemplate btnCustomFieldForOrg = new ButtonTemplate("Quản lý giao diện",FontAwesome.Solid.DISPLAY.create());

	public SettingView() {
		buildLayout();
		configComponent();

	}


	@Override
	public void buildLayout() {
		this.setSizeFull();

		btnRoleTemplate.setWidthFull();
		
		btnLogRequest.setWidthFull();
		
		btnOrgCatogory.setWidthFull();
		
		btnCustomFieldForOrg.setWidthFull();
		btnCustomFieldForOrg.setVisible(false);

		this.add(btnOrgCatogory,btnLogRequest,btnCustomFieldForOrg);

	}


	@Override
	public void configComponent() {
		btnRoleTemplate.addClickListener(e->{
			openDiaLogCreateOrg();
		});
		
		btnLogRequest.addClickListener(e->{
			openDialogViewLogRequest();
		});
		
		btnOrgCatogory.addClickListener(e->{
			openDialogViewOrgCategory();
		});
		
		btnCustomFieldForOrg.addClickListener(e->{
			openDialogCustomField();
		});
	}


	private void openDiaLogCreateOrg() {
		DialogTemplate dialog = new DialogTemplate("QUẢN LÝ VAI TRÒ MẪU",()->{

		});

		ControlRoleTemplateForm controlRoleTemplateForm = new ControlRoleTemplateForm();

		dialog.add(controlRoleTemplateForm);
		dialog.setWidth("100%");
		dialog.setHeight("99%");
		dialog.getFooter().removeAll();
		dialog.open();
	}
	
	private void openDialogViewLogRequest() {
		DialogTemplate dialog = new DialogTemplate("LỊCH SỬ SỰ DỤNG",()->{

		});

		ListLogRequestForm listLogRequestForm = new ListLogRequestForm();

		dialog.add(listLogRequestForm);
		dialog.setWidth("100%");
		dialog.setHeight("99%");
		dialog.getFooter().removeAll();
		dialog.open();
	}

	private void openDialogViewOrgCategory() {
		DialogTemplate dialogTemplate = new DialogTemplate("QUẢN LÝ LOẠI ĐƠN VỊ",()->{
			
		});
		
		OrganizationCategoryForm organizationCategoryForm = new OrganizationCategoryForm();
		dialogTemplate.add(organizationCategoryForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
		
		
	}
	
	private void openDialogCustomField() {
		DialogTemplate dialogTemplate = new DialogTemplate("CÀI ĐẶT GIAO DIỆN ĐƠN VỊ",()->{
			
		});
		
		CustomFieldForm customFieldForm = new CustomFieldForm();
		dialogTemplate.add(customFieldForm);
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.setSizeFull();
		dialogTemplate.getFooter().removeAll();
		dialogTemplate.open();
		
		
	}


}

package vn.com.ngn.page.organization_category;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
import vn.com.ngn.page.organization.forms.ListOrgForm;
import vn.com.ngn.views.MainLayout;

@Route(value = "org-category",layout = MainLayout.class)
@PageTitle("Quản lý loại đơn vị")
@PermitAll
public class OrgCategoryView extends VerticalLayout{
	private static final long serialVersionUID = 1L;
	
	public OrgCategoryView() {
		this.setSizeFull();
		this.setPadding(false);
		this.add(new ListOrgForm());
	}

}

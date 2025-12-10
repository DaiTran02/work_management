package vn.com.ngn.page.app_access;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
import vn.com.ngn.page.app_access.forms.AppAccessForm;
import vn.com.ngn.views.MainLayout;

@PermitAll
@Route(value = "app_access",layout = MainLayout.class)
@PageTitle(value = "Quản lý quyền truy cập")
public class AppAccessView extends VerticalLayout{
	private static final long serialVersionUID = 1L;

	public AppAccessView() {
		this.add(new AppAccessForm());
	}

}
